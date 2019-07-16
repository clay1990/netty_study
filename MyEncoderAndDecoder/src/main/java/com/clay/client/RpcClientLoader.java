package com.clay.client;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Auther: yuyao
 * @Date: 2019/5/29 16:12
 * @Description:
 */
public class RpcClientLoader {

    private static volatile RpcClientLoader rpcClientLoader;

    private ClientHandler messageSendHandler = null;
    private Lock lock = new ReentrantLock();
    private Condition handlerStatus = lock.newCondition();

    public static RpcClientLoader getInstance(){
        if (rpcClientLoader == null) {
            synchronized (RpcClientLoader.class) {
                if (rpcClientLoader == null) {
                    rpcClientLoader = new RpcClientLoader();
                }
            }
        }
        return rpcClientLoader;
    }

    public void setMessageSendHandler(ClientHandler messageInHandler) {
        try {
            lock.lock();
            System.out.println("设置handler拿到锁....");
            this.messageSendHandler = messageInHandler;
            handlerStatus.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public ClientHandler getMessageSendHandler() throws InterruptedException {
        try {
            lock.lock();
            if (messageSendHandler == null) {
                handlerStatus.await();
            }
            return messageSendHandler;
        } finally {
            lock.unlock();
        }
    }



}