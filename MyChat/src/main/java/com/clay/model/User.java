package com.clay.model;

import java.io.Serializable;

/**
 * @Auther: yuyao
 * @Date: 2019/6/4 13:52
 * @Description:
 */
public class User implements Serializable {
    private String id;
    private String userName;
    private String password;

    public User() {
    }

    public User(String id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}