package com.dhh.websocket.components;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.dhh.websocket.SocketManager;
import com.dhh.websocket.inter.IOnReceiveMessageListener;
import com.dhh.websocket.inter.IOnSocketConnectionStatusListener;
import com.dhh.websocket.inter.IRemoteService;
import com.dhh.websocket.inter.ISendMessageCallback;
import com.dhh.websocket.inter.OnSocketConnectionStatusListener;
import com.dhh.websocket.model.BaseTypeModel;
import com.dhh.websocket.model.ReceiveMessageModel;
import com.dhh.websocket.model.SendMessageModel;
import com.dhh.websocket.model.chat.PingModel;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;

import static com.dhh.websocket.SocketManager.SOCKET_STATUS_1;
import static com.dhh.websocket.SocketManager.SOCKET_STATUS_4;

public class SocketService extends Service implements SocketManager.OnSocketBaseTypeModelResultListener{
    private Gson gson = new Gson();
    private final  String VERIFICATION_PERMISSION= "com.gxx.websocket.permission.ACCESS_CONTACT_MANAGER";
    private RemoteCallbackList<IOnReceiveMessageListener> onReceiveMessageListenerRemoteCallbackList = new RemoteCallbackList<IOnReceiveMessageListener>();
    private RemoteCallbackList<IOnSocketConnectionStatusListener> onSocketConnectionStatusListenerRemoteCallbackList = new RemoteCallbackList<IOnSocketConnectionStatusListener>();
    private SocketManager socketManager = new SocketManager();
    private Handler mainHandler = new Handler();
    private boolean isLoopPing = true;
    private Thread thread = null;
    private LoopPingRnnable loopPingRnnable = new LoopPingRnnable(this);
    public static final int SOCKET_SERVICE_NETWORK_STATUS_200 = 200;//正常
    public static final int SOCKET_SERVICE_NETWORK_STATUS_201 = 201;//未连接上

    @Override
    public void onCreate() {
        super.onCreate();
        thread = new Thread(loopPingRnnable);
        thread.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (checkCallingOrSelfPermission(VERIFICATION_PERMISSION) == PackageManager.PERMISSION_DENIED) {
            Log.e("","onBind: 权限校验失败，拒绝绑定...");
            return null;
        }
        return new RemoteServiceStub(this);
    }


    public static class RemoteServiceStub extends IRemoteService.Stub {
        private WeakReference<SocketService> serviceWeakReference = null;

        public RemoteServiceStub(SocketService socketService) {
            serviceWeakReference = new WeakReference<SocketService>(socketService);
            serviceWeakReference.get().socketManager.setOnSocketBaseTypeModelResultListener(serviceWeakReference.get());
        }

        /**
         * @date 创建时间: 2021/3/30
         * @auther gaoxiaoxiong
         * @description 初始化websocket 并且监听网络的连接
         **/
        @Override
        public void connectService(String webSocketUrl) throws RemoteException {
            if (serviceWeakReference == null || serviceWeakReference.get() == null) {
                return;
            }
            serviceWeakReference.get().socketManager.init(webSocketUrl, new OnSocketConnectionStatusListener() {
                @Override
                public void onSocketConnectionStatus(int status) {
                    serviceWeakReference.get().onConnectionStatusChanged(status);
                }
            });
        }


        /**
         * @date 创建时间: 2021/3/30
         * @auther gaoxiaoxiong
         * @description 在这里处理发送消息
         **/
        @Override
        public void sendMessage(SendMessageModel messageModel, ISendMessageCallback iSendMessageCallback) throws RemoteException {
            if (serviceWeakReference == null || serviceWeakReference.get() == null) {
                return;
            }
            boolean isSuccess = serviceWeakReference.get().sendMessage(messageModel.getJsonMessage());
            //todo 这里处理发送失败
            if (iSendMessageCallback != null && iSendMessageCallback.asBinder().isBinderAlive() && !isSuccess) {//如果发送消息失败
                iSendMessageCallback.onFailure(messageModel.getMessageType(),messageModel.getJsonMessage(),-1);
            }
        }

        /**
         * @date 创建时间: 2021/3/30
         * @auther gaoxiaoxiong
         * @description 与前端绑定，接收到服务器传递的数据，回调使用
         **/
        @Override
        public void setOnReceiveMessageListener(IOnReceiveMessageListener listener) throws RemoteException {
            if (serviceWeakReference == null || serviceWeakReference.get() == null) {
                return;
            }
            serviceWeakReference.get().onReceiveMessageListenerRemoteCallbackList.register(listener);
        }

    }

    /**
     * @date 创建时间:2021/2/12
     * @auther gaoxiaoxiong
     * @description 网络连接状态回调给前端
     */
    private void onConnectionStatusChanged(int status) {
        int connectStatus = SOCKET_SERVICE_NETWORK_STATUS_201;
        if (status == SOCKET_STATUS_1 || status == SOCKET_STATUS_4) {//链接成功
            connectStatus = SOCKET_SERVICE_NETWORK_STATUS_200;
        }

        final int finalConnectStatus = connectStatus;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                int i = onSocketConnectionStatusListenerRemoteCallbackList.beginBroadcast();
                IOnSocketConnectionStatusListener listener;
                while (i > 0) {
                    i--;
                    listener = onSocketConnectionStatusListenerRemoteCallbackList.getBroadcastItem(i);
                    try {
                        listener.onSocketConnectionStatus(finalConnectStatus);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                onSocketConnectionStatusListenerRemoteCallbackList.finishBroadcast();
            }
        });
    }

    /**
     * @date 创建时间:2021/2/12
     * @auther gaoxiaoxiong
     * @description 请求结果处理
     */
    @Override
    public void onSocketBaseTypeModelResult(final String type, final String msg, final BaseTypeModel baseTypeModel) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                int i = onReceiveMessageListenerRemoteCallbackList.beginBroadcast();
                IOnReceiveMessageListener listener;
                while (i > 0) {
                    i--;
                    listener = onReceiveMessageListenerRemoteCallbackList.getBroadcastItem(i);
                    ReceiveMessageModel receiveMessageModel = new ReceiveMessageModel();
                    try {
                        String jsonDataString = gson.toJson(baseTypeModel);
                        if (jsonDataString.getBytes().length / 1024 > 200) {
                            //todo 请自行处理这里的数据，最好的办法是将此次数据，存入本地数据库。多进程，是无法处理大量数据的传输的
                        } else {

                        }
                        listener.resultRequestData(receiveMessageModel);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                onReceiveMessageListenerRemoteCallbackList.finishBroadcast();
            }
        });
    }

    /**
     * @date 创建时间:2021/2/12
     * @auther gaoxiaoxiong
     * @description 发送消息
     */
    public boolean sendMessage(String jsonString) {
        return socketManager.sendMessage(jsonString);
    }

    /**
     * @date 创建时间: 2021/3/30
     * @auther gaoxiaoxiong
     * @description 定时返送与服务器定义的协议 ping
     **/
    static class LoopPingRnnable implements Runnable {
        private WeakReference<SocketService> serviceWeakReference;
        final PingModel pingModel = new PingModel();

        public LoopPingRnnable(SocketService socketService) {
            serviceWeakReference = new WeakReference<SocketService>(socketService);
            pingModel.setType("ping");
        }

        @Override
        public void run() {
            try {
                while (serviceWeakReference != null || serviceWeakReference.get() != null && serviceWeakReference.get().isLoopPing && !Thread.currentThread().isInterrupted()) {
                    serviceWeakReference.get().sendMessage(serviceWeakReference.get().gson.toJson(pingModel));
                    Thread.sleep(5 * 1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isLoopPing = false;
        if (thread != null) {
            thread.interrupt();
        }

        if (onSocketConnectionStatusListenerRemoteCallbackList != null) {
            onSocketConnectionStatusListenerRemoteCallbackList.kill();
        }

        if (onReceiveMessageListenerRemoteCallbackList != null) {
            onReceiveMessageListenerRemoteCallbackList.kill();
        }

        if (socketManager != null) {
            socketManager.destory();
        }
    }
}
