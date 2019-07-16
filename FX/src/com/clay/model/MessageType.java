package com.clay.model;

/**
 * @Auther: yuyao
 * @Date: 2019/6/4 14:13
 * @Description:
 */
public enum MessageType {

    ONLINE("online"),OFFLINE("offline"),GROUP("group"),PRIVATE("private"),FRIENDS("friends");

    private String type;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    MessageType(String type){
        this.type = type;
    }
}