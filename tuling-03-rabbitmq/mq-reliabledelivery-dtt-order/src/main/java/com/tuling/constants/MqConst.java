package com.tuling.constants;

/**
* @vlog: 高于生活，源于生活
* @desc: 类的描述:
* @author: smlz
* @createDate: 2019/10/11 16:41
* @version: 1.0
*/
public class MqConst {

    /**交换机名称*/
    public static final String ORDER_TO_PRODUCT_EXCHANGE_NAME = "order-to-product.exchange";

    /**队列名称*/
    public static final String ORDER_TO_PRODUCT_QUEUE_NAME = "order-to-product.queue";

    /**路由key*/
    public static final String ORDER_TO_PRODUCT_ROUTING_KEY = "order-to-product.key";

    /**消息重发的最大次数*/
    public static final Integer MSG_RETRY_COUNT = 5;

    public static final Integer TIME_DIFF = 30;
}
