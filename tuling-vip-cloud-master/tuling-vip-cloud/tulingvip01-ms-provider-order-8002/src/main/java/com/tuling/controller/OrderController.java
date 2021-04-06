package com.tuling.controller;

import com.netflix.discovery.converters.Auto;
import com.tuling.entity.OrderVo;
import com.tuling.service.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by smlz on 2019/3/26.
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderServiceImpl orderServiceImpl;

    @Autowired
    private ServiceInstance serviceInstance;

    @RequestMapping("/queryOrdersByUserId/{userId}")
    public List<OrderVo> queryOrdersByUserId(@PathVariable("userId") Integer userId) {
        return orderServiceImpl.queryOrdersByUserId(userId);
    }

    @RequestMapping("/getRegisterInfo")
    public String info() {
        return serviceInstance.getHost()+":"+serviceInstance.getPort();


    }
}
