package com.clay.model;

import java.io.Serializable;

/**
 * @Auther: yuyao
 * @Date: 2019/6/3 16:53
 * @Description:
 */
public class MessageRequest implements Serializable {

    private String userId;
    private String name;
    private Object msg;
    private String sendTo;
    private String msgType;
    private String time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "MessageRequest{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", msg='" + msg + '\'' +
                ", sendTo='" + sendTo + '\'' +
                ", msgType='" + msgType + '\'' +
                '}';
    }
}