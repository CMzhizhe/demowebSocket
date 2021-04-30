package com.dhh.websocket.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ReceiveMessageModel implements Parcelable {
    private String type;
    private String msg;
    private String jsonMessage;
    private String messageUid ;
    private Long sessionId;

    public ReceiveMessageModel() {
        super();
    }

    protected ReceiveMessageModel(Parcel in) {
        type = in.readString();
        msg = in.readString();
        jsonMessage = in.readString();
        messageUid = in.readString();
        if (in.readByte() == 0) {
            sessionId = null;
        } else {
            sessionId = in.readLong();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(msg);
        dest.writeString(jsonMessage);
        dest.writeString(messageUid);
        if (sessionId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(sessionId);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReceiveMessageModel> CREATOR = new Creator<ReceiveMessageModel>() {
        @Override
        public ReceiveMessageModel createFromParcel(Parcel in) {
            return new ReceiveMessageModel(in);
        }

        @Override
        public ReceiveMessageModel[] newArray(int size) {
            return new ReceiveMessageModel[size];
        }
    };

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getJsonMessage() {
        return jsonMessage;
    }

    public void setJsonMessage(String jsonMessage) {
        this.jsonMessage = jsonMessage;
    }

    public String getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(String messageUid) {
        this.messageUid = messageUid;
    }
}
