package com.clay.server;

import com.clay.inter.OrderService;
import com.clay.server.serviceImpl.OrderServiceImpl;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: yuyao
 * @Date: 2019/5/30 11:22
 * @Description:
 */
public class BeanLoader {

    private static  volatile  BeanLoader beanLoader;
    public static  volatile ConcurrentHashMap<String,Object> beanMap = new ConcurrentHashMap<>();


    static {
        beanMap.put(OrderService.class.getName() , new OrderServiceImpl());
    }

    public static BeanLoader getInstance(){
        if(beanLoader == null){
            synchronized (BeanLoader.class){
                if(beanLoader == null){
                    beanLoader = new BeanLoader();
                }
            }
        }

        return beanLoader;
    }

}