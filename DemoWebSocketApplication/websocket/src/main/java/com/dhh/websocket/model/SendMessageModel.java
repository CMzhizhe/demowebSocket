package com.dhh.websocket.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @date 创建时间:2021/2/22 0022
 * @auther gaoxiaoxiong
 * @Descriptiion 自定义发送的消息
 **/
public class SendMessageModel implements Parcelable {
    private String jsonMessage;
    private String messageType;
    private String messageUid ;

    public SendMessageModel() {
    }


    protected SendMessageModel(Parcel in) {
        jsonMessage = in.readString();
        messageType = in.readString();
        messageUid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(jsonMessage);
        dest.writeString(messageType);
        dest.writeString(messageUid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SendMessageModel> CREATOR = new Creator<SendMessageModel>() {
        @Override
        public SendMessageModel createFromParcel(Parcel in) {
            return new SendMessageModel(in);
        }

        @Override
        public SendMessageModel[] newArray(int size) {
            return new SendMessageModel[size];
        }
    };

    public String getJsonMessage() {
        return jsonMessage;
    }

    public void setJsonMessage(String jsonMessage) {
        this.jsonMessage = jsonMessage;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(String messageUid) {
        this.messageUid = messageUid;
    }
}
