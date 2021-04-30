package com.dhh.websocket;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.dhh.websocket.base.RxWebSocketUtil;
import com.dhh.websocket.base.WebSocketSubscriber;
import com.dhh.websocket.inter.OnSocketConnectionStatusListener;
import com.dhh.websocket.model.BaseTypeModel;
import com.dhh.websocket.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okio.ByteString;

//https://github.com/dhhAndroid/RxWebSocket/tree/2.x

/**
 * @date 创建时间: 2021/3/30
 * @auther gaoxiaoxiong
 * @description 这个类，是统一管理sock的类。
 **/
public class SocketManager {
    private static SocketManager socketManager;
    public static final int SOCKET_STATUS_4 = 4;//重连成功
    private static final int SOCKET_STATUS_2 = 2;//重新连接中
    public static final int SOCKET_STATUS_1 = 1;//成功
    private final int SOCKET_STATUS_0 = 0;//失败
    private int currentSocketStatus = SOCKET_STATUS_0;
    private WeakReference<OnSocketBaseTypeModelResultListener> onSocketBaseTypeModelResultListenerWeakReference;
    private Disposable disposable = null;


    public void setOnSocketBaseTypeModelResultListener(OnSocketBaseTypeModelResultListener onSocketBaseTypeModelResultListener) {
        onSocketBaseTypeModelResultListenerWeakReference = new WeakReference<OnSocketBaseTypeModelResultListener>(onSocketBaseTypeModelResultListener);
    }

    public interface OnSocketBaseTypeModelResultListener {
        void onSocketBaseTypeModelResult(String type, String msg, BaseTypeModel baseTypeModel);
    }


    public void init(String wsUrl, final OnSocketConnectionStatusListener onSocketConnectionStatusListener) {
        if (TextUtils.isEmpty(wsUrl)) {
            return;
        }
        OkHttpClient mClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        RxWebSocketUtil instance = RxWebSocketUtil.getInstance();
        instance.setClient(mClient);
        instance.setReconnectInterval(5, TimeUnit.SECONDS);

        RxWebSocketUtil.getInstance().getWebSocketInfo(wsUrl)
                .subscribe(new WebSocketSubscriber() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        SocketManager.this.disposable = d;
                    }

                    @Override
                    public void onOpen(@NonNull WebSocket webSocket) {
                        LogUtils.e("连接成功");
                        if (currentSocketStatus == SOCKET_STATUS_2) {//重新连接成功
                            if (onSocketConnectionStatusListener != null) {
                                onSocketConnectionStatusListener.onSocketConnectionStatus(SOCKET_STATUS_4);
                            }
                            currentSocketStatus = SOCKET_STATUS_1;
                        } else {
                            currentSocketStatus = SOCKET_STATUS_1;
                            if (onSocketConnectionStatusListener != null) {
                                onSocketConnectionStatusListener.onSocketConnectionStatus(SOCKET_STATUS_1);
                            }
                        }
                    }

                    @Override
                    public void onMessage(@NonNull BaseTypeModel baseTypeModel) {
                        if (onSocketBaseTypeModelResultListenerWeakReference != null && onSocketBaseTypeModelResultListenerWeakReference.get() != null) {
                            if (baseTypeModel.getData() != null) {
                                String pritlnMessage = String.format("type: %s ， onMessage返回的数据: %s", baseTypeModel.getType(), baseTypeModel.getData().toString());
                                LogUtils.gxx_all_errorLog(pritlnMessage);
                            }
                            onSocketBaseTypeModelResultListenerWeakReference.get().onSocketBaseTypeModelResult(baseTypeModel.getType(), baseTypeModel.getMsg(), baseTypeModel);
                        }
                    }

                    @Override
                    public void onMessage(@NonNull ByteString byteString) {

                    }

                    @Override
                    protected void onReconnect() {
                        LogUtils.e("onReconnect 重连");
                        currentSocketStatus = SOCKET_STATUS_2;
                        if (onSocketConnectionStatusListener != null) {
                            onSocketConnectionStatusListener.onSocketConnectionStatus(SOCKET_STATUS_2);
                        }
                    }
                });
    }


    /**
     * @date 创建时间:2021/1/15 0015
     * @auther gaoxiaoxiong
     * @Descriptiion 发送消息
     **/
    public boolean sendMessage(String jsonString) {
        if (currentSocketStatus == SOCKET_STATUS_1) {
            return RxWebSocketUtil.getInstance().send(jsonString);
        } else {
            return false;
        }
    }

    /**
     * @date 创建时间: 2021/3/30
     * @auther gaoxiaoxiong
     * @description 销毁
     **/
    public void destory() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        RxWebSocketUtil.getInstance().destory();
    }

}
