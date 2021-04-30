package com.dhh.websocket.model.chat.amiable;

public class AmiSconversationModel {
    private Long id;
    private Long fromid;
    private Long toid;
    private String log_type;
    private String log_old_time;
    private Long microtime;
    private String log_created_at;
    private String visitor_name;
    private String visitor_avatar;
    private String messageUid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromid() {
        return fromid;
    }

    public void setFromid(Long fromid) {
        this.fromid = fromid;
    }

    public Long getToid() {
        return toid;
    }

    public void setToid(Long toid) {
        this.toid = toid;
    }

    public String getLog_type() {
        return log_type;
    }

    public void setLog_type(String log_type) {
        this.log_type = log_type;
    }

    public String getLog_old_time() {
        return log_old_time;
    }

    public void setLog_old_time(String log_old_time) {
        this.log_old_time = log_old_time;
    }

    public Long getMicrotime() {
        return microtime;
    }

    public void setMicrotime(Long microtime) {
        this.microtime = microtime;
    }

    public String getLog_created_at() {
        return log_created_at;
    }

    public void setLog_created_at(String log_created_at) {
        this.log_created_at = log_created_at;
    }

    public String getVisitor_name() {
        return visitor_name;
    }

    public void setVisitor_name(String visitor_name) {
        this.visitor_name = visitor_name;
    }

    public String getVisitor_avatar() {
        return visitor_avatar;
    }

    public void setVisitor_avatar(String visitor_avatar) {
        this.visitor_avatar = visitor_avatar;
    }

    public String getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(String messageUid) {
        this.messageUid = messageUid;
    }
}
