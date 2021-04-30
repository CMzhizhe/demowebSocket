// ISendMessageCallback.aidl
package com.dhh.websocket.inter;

// Declare any non-default types here with import statements

interface ISendMessageCallback {
    void onSuccess(String messageUid,String messageType);
    void onFailure(String messageType,String jsonString,int errorCode);
}