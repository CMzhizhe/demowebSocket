// IRemoteService.aidl
package com.dhh.websocket.inter;
import com.dhh.websocket.model.SendMessageModel;
import com.dhh.websocket.inter.ISendMessageCallback;
import com.dhh.websocket.inter.IOnReceiveMessageListener;
interface IRemoteService {
   oneway void connectService(in String webSocketUrl);

   oneway void sendMessage(in SendMessageModel messageModel, in ISendMessageCallback iSendMessageCallback);

   void setOnReceiveMessageListener(in IOnReceiveMessageListener listener);


}