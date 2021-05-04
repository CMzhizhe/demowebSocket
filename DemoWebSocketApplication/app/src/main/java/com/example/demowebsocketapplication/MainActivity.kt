package com.example.demowebsocketapplication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.text.TextUtils
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.dhh.websocket.base.RxWebSocketUtil
import com.dhh.websocket.components.SocketService
import com.dhh.websocket.inter.IOnReceiveMessageListener
import com.dhh.websocket.inter.IRemoteService
import com.dhh.websocket.inter.ISendMessageCallback
import com.dhh.websocket.model.BaseTypeModel
import com.dhh.websocket.model.ReceiveMessageModel
import com.dhh.websocket.model.SendMessageModel
import com.google.gson.Gson
import com.google.gson.JsonParser

class MainActivity : AppCompatActivity() {
    private var remoteServiceStub: IRemoteService? = null
    private var mainHandler: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainHandler = Handler(mainLooper)
        val intent = Intent(this, SocketService::class.java);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {

        }

        override fun onServiceConnected(name: ComponentName, iBinder: IBinder?) {
            if (remoteServiceStub == null) {
                remoteServiceStub = SocketService.RemoteServiceStub.asInterface(iBinder)
            }
            remoteServiceStub?.let {
                it.connectService("xxxxxxxx");//设置连接地址
                //设置消息接受的监听
                it.setOnReceiveMessageListener(object : IOnReceiveMessageListener.Stub() {
                    override fun resultRequestData(receiveMessageModel: ReceiveMessageModel) {
                        this@MainActivity.doSucessResultData(receiveMessageModel);
                    }
                })
            }
        }
    }

    /**
    * @date 创建时间:2021/5/4
    * @auther gaoxiaoxiong
    * @description 用于处理发送消息
    */
    private fun sendMessage(value:String){
        if (remoteServiceStub != null && remoteServiceStub!!.asBinder().isBinderAlive) {
            val sendMessageModel = SendMessageModel();
            sendMessageModel.jsonMessage = Gson().toJson(value);
            remoteServiceStub!!.sendMessage(sendMessageModel, object : ISendMessageCallback.Stub() {
                override fun onSuccess(messageUid: String, messageType: String) {

                }

                //这里处理消息发送失败的地方
                override fun onFailure(messageType: String, jsonString: String, errorCode: Int) {
                    doSendMessageError(messageType, jsonString, errorCode);
                }
            })
        }
    }

    /**
     * @date 创建时间:2021/3/13
     * @auther gaoxiaoxiong
     * @description 发送消息失败
     */
    private fun doSendMessageError(messageType: String, jsonString: String, errorCode: Int) {
        mainHandler?.post(object : Runnable {
            override fun run() {

            }
        })
    }

    /**
     * @date 创建时间:2021/2/24 0024
     * @auther gaoxiaoxiong
     * @Descriptiion 处理由socket返回的数据，注意此时还是处于binder池里面
     **/
    private fun doSucessResultData(receiveMessageModel: ReceiveMessageModel) {
        val baseTypeModel = BaseTypeModel();
        val parser = JsonParser()
        if (!TextUtils.isEmpty(receiveMessageModel.messageUid)) {//这里是因为数据量太大，所以需要从数据库中获取
           /* val daoTransmissionModel = DaoManagerUtils.getInstance().loadDaoTransmissionModel(receiveMessageModel.messageUid.toLong());
            if (daoTransmissionModel == null) {
                return
            }
            val jsonObject = parser.parse(daoTransmissionModel.jsonString).asJsonObject
            if (jsonObject!!.get(RxWebSocketUtil.RESULT_DATA).isJsonObject) {
                baseTypeModel.data = jsonObject.get(RxWebSocketUtil.RESULT_DATA).asJsonObject;
            } else if (jsonObject.get(RxWebSocketUtil.RESULT_DATA).isJsonArray) {
                baseTypeModel.data = jsonObject.get(RxWebSocketUtil.RESULT_DATA).asJsonArray;
            } else {
                baseTypeModel.data = jsonObject.get(RxWebSocketUtil.RESULT_DATA).asString;
            }
            baseTypeModel.msg = jsonObject.get(RxWebSocketUtil.RESULT_MSG).asString;
            baseTypeModel.type = jsonObject.get(RxWebSocketUtil.RESULT_TYPE).asString;*/
        } else {
            if (receiveMessageModel.jsonMessage != null) {
                val jsonElement = parser.parse(receiveMessageModel.jsonMessage);
                if (jsonElement.isJsonObject) {
                    baseTypeModel.data = jsonElement.asJsonObject;
                } else if (jsonElement.isJsonArray) {
                    baseTypeModel.data = jsonElement.asJsonArray
                } else {
                    baseTypeModel.data = jsonElement.asString
                }
            }
            baseTypeModel.msg = receiveMessageModel.msg;
            baseTypeModel.type = receiveMessageModel.type;
        }

        //将结果返回给主handle
        mainHandler?.post(object : Runnable {
            override fun run() {

            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection);
    }

}