package com.clay.server.serviceImpl;

import com.clay.inter.OrderService;

/**
 * @Auther: yuyao
 * @Date: 2019/5/30 10:53
 * @Description:
 */
public class OrderServiceImpl implements OrderService {
    @Override
    public String order(Integer userId, Integer productId, Integer num) {
        return "用户：[" + userId + "],购买产品：[" + productId + "],一共买了" + num + "个。";
    }
}