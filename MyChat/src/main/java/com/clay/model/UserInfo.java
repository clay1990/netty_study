package com.clay.model;

import io.netty.channel.Channel;

/**
 * @Auther: yuyao
 * @Date: 2019/6/4 15:06
 * @Description:
 */
public class UserInfo {

    private User user;

    private Channel channel;

    public UserInfo() { }

    public UserInfo(User user, Channel channel) {
        this.user = user;
        this.channel = channel;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}