package com.dhh.websocket.model.chat

class PushConversationBean {
    /**
     * app_name : 微聚新零售
     * id : 9390
     * log_created_at : 2021-03-18 18:09:37
     * log_is_read : 0
     * log_msg : 1
     * log_type : text
     * microtime : 1616062177812532
     * server_id : 147
     * visitor_avatar : http://im.cdn.milankeji.cn/78444aef41495512677211902402c700
     * visitor_id : 1093
     * visitor_name : 访客c56c95
     */
    var app_name: String? = null
    var id = 0L
    var log_created_at: String? = null
    var log_is_read = 0
    var log_msg: String? = null
    var log_type: String? = null
    var microtime: Long=0L
    var server_id = 0L
    var visitor_avatar: String? = null
    var visitor_id = 0L
    var visitor_name: String? = null
    var visitor_desc:String?=null
    var fromid:Long = 0L
    var toid:Long = 0L
}