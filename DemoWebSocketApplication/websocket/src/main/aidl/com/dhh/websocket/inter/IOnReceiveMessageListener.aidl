package com.dhh.websocket.inter;

import com.dhh.websocket.model.ReceiveMessageModel;

interface IOnReceiveMessageListener {
    void resultRequestData(in ReceiveMessageModel receiveMessageModel);
}