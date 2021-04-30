package com.dhh.websocket.base;

import android.os.SystemClock;
import android.text.TextUtils;

import com.dhh.websocket.exceptionhandle.ExceptionHandle;
import com.dhh.websocket.model.BaseTypeModel;
import com.dhh.websocket.utils.LogUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;



/**
 * Created by dhh on 2017/9/21.
 * <p>
 * WebSocketUtil based on okhttp and RxJava
 * </p>
 * Core Feature : WebSocket will be auto reconnection onFailed.
 */

public class RxWebSocketUtil {
    public static final String RESULT_TYPE = "type";
    public static final String RESULT_MSG = "msg";
    public static final String RESULT_DATA="data";

    private static RxWebSocketUtil instance;
    private OkHttpClient client;
    private WebSocket webSocket;
    private long interval = 1;
    private volatile boolean isConnect = false;
    private TimeUnit reconnectIntervalTimeUnit = TimeUnit.SECONDS;


    public static RxWebSocketUtil getInstance() {
        if (instance == null) {
            synchronized (RxWebSocketUtil.class) {
                if (instance == null) {
                    instance = new RxWebSocketUtil();
                }
            }
        }
        return instance;
    }

    /**
     * set your client
     *
     * @param client
     */
    public void setClient(OkHttpClient client) {
        if (client == null) {
            throw new NullPointerException(" Are you kidding me ? client == null");
        }
        this.client = client;
    }

    public void setReconnectInterval(long interval, TimeUnit timeUnit) {
        this.interval = interval;
        this.reconnectIntervalTimeUnit = timeUnit;

    }

    public Observable<WebSocketInfo> getWebSocketInfo(final String url, final long timeout, final TimeUnit timeUnit) {
        return Observable.create(new WebSocketOnSubscribe(url))
                //自动重连
                .timeout(timeout, timeUnit)
                .retry(new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Exception {
                        return throwable instanceof IOException || throwable instanceof TimeoutException || throwable instanceof ExceptionHandle.ServerException;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * default timeout: 30 days
     * <p>
     * 若忽略小米平板,请调用这个方法
     * </p>
     */
    public Observable<WebSocketInfo> getWebSocketInfo(String url) {
        return getWebSocketInfo(url, 30, TimeUnit.DAYS);
    }

    /**
     * 如果url的WebSocket已经打开,可以直接调用这个发送消息.
     * @param msg
     */
    public boolean send(String msg) {
        if (webSocket != null && isConnect) {
            LogUtils.e("传输数据 = " + msg);
            boolean isSuccess = webSocket.send(msg);
            if (!isSuccess) {
                LogUtils.e("消息发送失败");
                LogUtils.e("失败websocket地址" + webSocket.toString());
            }
            return isSuccess;
        }else {
            return false;
        }
    }


    private Request getRequest(String url) {
        return new Request.Builder().get().url(url).build();
    }

    private  class WebSocketOnSubscribe implements ObservableOnSubscribe<WebSocketInfo> {
        private String url;
        public WebSocketOnSubscribe(String url) {
            this.url = url;
        }

        @Override
        public void subscribe(@NonNull ObservableEmitter<WebSocketInfo> emitter) throws Exception {
            if (webSocket!=null && !isConnect){//重连才会走这一步
                emitter.onNext(WebSocketInfo.createReconnect());//重连
                long ms = reconnectIntervalTimeUnit.toMillis(interval);
                if (ms == 0) {
                    ms = 1000;
                }
                SystemClock.sleep(ms);
            }
            initWebSocket(emitter);
        }

        private void initWebSocket(final ObservableEmitter<WebSocketInfo> emitter) {
            webSocket = client.newWebSocket(getRequest(url), new WebSocketListener() {
                @Override
                public void onOpen(final WebSocket webSocket, Response response) {
                    isConnect = response.code() == 101;
                    if (!isConnect) {
                        webSocket.close(1000, "没有连接成功");
                        if (!emitter.isDisposed()){
                            emitter.onError(new ExceptionHandle.ServerException(-100, "没有连接成功"));
                        }
                    } else {
                        if (!emitter.isDisposed()) {
                            emitter.onNext(new WebSocketInfo(webSocket, true));
                        }
                    }
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    //todo 可以在这里处理，你的代码，请自行修改
                    if (!emitter.isDisposed() && !TextUtils.isEmpty(text)) {
                        JsonObject jsonObject = new JsonParser().parse(text).getAsJsonObject();
                        String type  = jsonObject.get(RESULT_TYPE).getAsString();
                        String msg = "";

                        BaseTypeModel baseTypeModel = new BaseTypeModel();
                        if (jsonObject.has(RESULT_MSG)) {
                            msg = jsonObject.get(RESULT_MSG).getAsString();
                        }
                        baseTypeModel.setMsg(msg);
                        baseTypeModel.setType(type);
                        if (jsonObject.has(RESULT_DATA) && jsonObject.get(RESULT_DATA)!=null && !jsonObject.get(RESULT_DATA).isJsonNull()){
                            if (jsonObject.get(RESULT_DATA).isJsonArray()){
                                baseTypeModel.setData(jsonObject.get(RESULT_DATA).getAsJsonArray());
                            }else if (jsonObject.get(RESULT_DATA).isJsonObject()){
                                baseTypeModel.setData(jsonObject.get(RESULT_DATA).getAsJsonObject());
                            }
                            emitter.onNext(new WebSocketInfo(webSocket, baseTypeModel));
                        }else {
                            baseTypeModel.setData(null);
                            emitter.onNext(new WebSocketInfo(webSocket, baseTypeModel));
                        }
                    }
                }

                @Override
                public void onMessage(WebSocket webSocket, ByteString bytes) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(new WebSocketInfo(webSocket, bytes));
                    }
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    isConnect = false;
                    LogUtils.e("WebSocket 连接失败异常原因：" + t.getMessage());
                    webSocket.close(1000, "没有连接成功");
                    if (!emitter.isDisposed()) {
                        emitter.onError(t);
                    }
                }

                //收到服务端发来的 CLOSE 帧消息，准备关闭连接
                //onClosing是当远程端暗示没有数据交互时回调（即此时准备关闭，但连接还没有关闭）
                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    isConnect = false;
                    LogUtils.e("收到服务端发来的 CLOSE 帧消息，webSocket 地址" + webSocket.toString());
                    webSocket.close(1001, "服务器返回错误");
                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    isConnect = false;
                    LogUtils.e("onClosed:code= " + code);
                    LogUtils.e("onClosed:reason= " + reason);
                }
            });
        }
    }


    /**
     * @date 创建时间:2021/1/19 0019
     * @auther gaoxiaoxiong
     * @Descriptiion 销毁
     **/
    public void destory() {
        if (webSocket != null) {
            isConnect = false;
            webSocket.cancel();
            webSocket.close(1000, "客户端主动关闭连接");
            webSocket = null;
        }
    }
}
