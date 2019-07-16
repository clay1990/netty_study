package com.clay.client;

import com.clay.common.MessageCallBack;
import com.clay.common.MessageRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @Auther: yuyao
 * @Date: 2019/5/29 10:49
 * @Description:
 */
public class MessageSendInvocationHandler implements InvocationHandler {



    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setMessageId(UUID.randomUUID().toString());
        messageRequest.setClassName(method.getDeclaringClass().getName());
        messageRequest.setMethodName(method.getName());
        messageRequest.setTypeParameters(method.getParameterTypes());
        messageRequest.setParametersVal(args);


        ClientHandler messageSendHandler = RpcClientLoader.getInstance().getMessageSendHandler();
        MessageCallBack callBack = messageSendHandler.sendToServer(messageRequest);
        Object response =  callBack.start();
        System.out.println( method.getDeclaringClass().getName() + "." + method.getName() + ">>>>>>>>>调用返回 ： " + response.toString());
        return response;

    }


}