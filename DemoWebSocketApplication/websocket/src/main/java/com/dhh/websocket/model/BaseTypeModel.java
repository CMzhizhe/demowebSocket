package com.dhh.websocket.model;

import android.os.Parcel;

public  class BaseTypeModel {
   private Object data;

   private String type;
   private String msg;

    public BaseTypeModel() {
    }

    protected BaseTypeModel(Parcel in) {
        type = in.readString();
        msg = in.readString();
    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
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
}
