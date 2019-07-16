package com.clay.common;

import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: yuyao
 * @Date: 2019/6/6 15:42
 * @Description:
 */
public class PingMessage implements Serializable {

    private String messageId;
    private String message;
    private Long time = new Date().getTime();

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "PingMessage{" +
                "messageId='" + messageId + '\'' +
                ", message='" + message + '\'' +
                ", time=" + time +
                '}';
    }
}