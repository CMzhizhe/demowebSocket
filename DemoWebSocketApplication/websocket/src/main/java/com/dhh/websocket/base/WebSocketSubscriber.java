package com.dhh.websocket.base;


import com.dhh.websocket.model.BaseTypeModel;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import okhttp3.WebSocket;
import okio.ByteString;

/**
 * Created by dhh on 2017/10/24.
 * <p>
 * override the method of you want to use
 * <p>
 * 根据业务需求重写你想使用的方法
 */

public abstract class WebSocketSubscriber implements Observer<WebSocketInfo> {

    @Override
    public final void onNext(@NonNull WebSocketInfo webSocketInfo) {
        if (webSocketInfo.isOnOpen()) {
            onOpen(webSocketInfo.getWebSocket());
        } else if (webSocketInfo.getBaseTypeModel() != null) {
            onMessage(webSocketInfo.getBaseTypeModel());
        } else if (webSocketInfo.getByteString() != null) {
            onMessage(webSocketInfo.getByteString());
        } else if (webSocketInfo.isOnReconnect()) {
            onReconnect();
        }
    }

    /**
     * Callback when the WebSocket is opened
     *
     * @param webSocket
     */
    protected void onOpen(@NonNull WebSocket webSocket) {
    }

    protected void onMessage(@NonNull BaseTypeModel baseTypeModel) {
    }

    protected void onMessage(@NonNull ByteString byteString) {
    }

    /**
     * Callback when the WebSocket is reconnecting
     */
    protected void onReconnect() {
    }

    @Override
    public final void onComplete() {

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

}
