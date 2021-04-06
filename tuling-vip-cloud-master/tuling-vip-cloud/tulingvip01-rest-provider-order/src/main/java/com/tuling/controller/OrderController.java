package com.tuling.controller;

import com.tuling.entity.OrderVo;
import com.tuling.service.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping("/queryOrdersByUserId/{userId}")
    public List<OrderVo> queryOrdersByUserId(@PathVariable("userId") Integer userId) {
        return orderServiceImpl.queryOrdersByUserId(userId);
    }
}
