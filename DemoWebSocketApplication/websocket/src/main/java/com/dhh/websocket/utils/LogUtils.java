package com.dhh.websocket.utils;

import android.util.Log;


public class LogUtils {
    private static final String TAG = "SocketLog";

    public static void e(String msg){
        Log.e(TAG,msg);
    }

    /**
     * 截断输出日志
     * @param msg
     */
    public static void gxx_all_errorLog(String msg) {
        int segmentSize = 3 * 1024;
        long length = msg.length();
        if (length <= segmentSize ) {// 长度小于等于限制直接打印
            Log.e(TAG, msg);
        }else {
            while (msg.length() > segmentSize ) {// 循环分段打印日志
                String logContent = msg.substring(0, segmentSize );
                msg = msg.replace(logContent, "");
                Log.e(TAG, logContent);//分段打印
            }
            Log.e(TAG, msg);
        }
    }
}
