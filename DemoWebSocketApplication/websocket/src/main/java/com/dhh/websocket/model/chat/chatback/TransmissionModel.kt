package com.dhh.websocket.model.chat.chatback

import android.os.Parcel
import android.os.Parcelable

class TransmissionModel() : Parcelable {
    var jsonSessionList:String? = "";
    var jsonChatDetailList:String? = "";
    var type:String = "";

    constructor(parcel: Parcel) : this() {
        jsonSessionList = parcel.readString()
        jsonChatDetailList = parcel.readString()
        type = parcel.readString()!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(jsonSessionList)
        parcel.writeString(jsonChatDetailList)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TransmissionModel> {
        override fun createFromParcel(parcel: Parcel): TransmissionModel {
            return TransmissionModel(parcel)
        }

        override fun newArray(size: Int): Array<TransmissionModel?> {
            return arrayOfNulls(size)
        }
    }

}