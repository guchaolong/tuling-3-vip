package com.redisson;

import org.redisson.Redisson;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private Redisson redisson;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/deduct_stock")
    public String deductStock() throws InterruptedException {
        String lockKey = "product_001";
        //String clientId = UUID.randomUUID().toString();
        RLock redissonLock = redisson.getLock(lockKey);
        try {
            //Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "zhuge"); //jedis.setnx(key,value)
            //stringRedisTemplate.expire(lockKey,30, TimeUnit.SECONDS);
            /*Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, clientId, 30, TimeUnit.SECONDS);

            if (!result) {
                return "1001";
            }*/

            // 加锁，实现锁续命功能
            redissonLock.lock();
            int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock")); // jedis.get("stock")
            if (stock > 0) {
                int realStock = stock - 1;
                stringRedisTemplate.opsForValue().set("stock", realStock + ""); // jedis.set(key,value)
                System.out.println("扣减成功，剩余库存:" + realStock + "");
            } else {
                System.out.println("扣减失败，库存不足");
            }
        } finally {
            redissonLock.unlock();
            /*if (clientId.equals(stringRedisTemplate.opsForValue().get(lockKey))){
                    stringRedisTemplate.delete(lockKey);
            }*/
        }
        return "end";
    }


    @RequestMapping("/redlock")
    public String redlock() throws InterruptedException {
        String lockKey = "product_001";
        //这里需要自己实例化不同redis实例的redisson客户端连接，这里只是伪代码用一个redisson客户端简化了
        RLock lock1 = redisson.getLock(lockKey);
        RLock lock2 = redisson.getLock(lockKey);
        RLock lock3 = redisson.getLock(lockKey);

        /**
         * 根据多个 RLock 对象构建 RedissonRedLock （最核心的差别就在这里）
         */
        RedissonRedLock redLock = new RedissonRedLock(lock1, lock2, lock3);
        try {
            /**
             * 4.尝试获取锁
             * waitTimeout 尝试获取锁的最大等待时间，超过这个值，则认为获取锁失败
             * leaseTime   锁的持有时间,超过这个时间锁会自动失效（值应设置为大于业务处理的时间，确保在锁有效期内业务能处理完）
             */
            boolean res = redLock.tryLock(10, 30, TimeUnit.SECONDS);
            if (res) {
                //成功获得锁，在这里处理业务
            }
        } catch (Exception e) {
            throw new RuntimeException("lock fail");
        } finally {
            //无论如何, 最后都要解锁
            redLock.unlock();
        }

        return "end";
    }


    /**
     * 读写锁， 解决双写一致性
     *
     * @param clientId
     * @return
     */

    //查库存
    @RequestMapping("/get_stock")
    public String getStock(@RequestParam("clientId") Long clientId) throws Exception {
        String lockKey = "product_stock_101";

        //获取读写锁
        RReadWriteLock readWriteLock = redisson.getReadWriteLock(lockKey);
        //获取读锁, 并且这把锁的模式标记为read   mode = read
        RLock rLock = readWriteLock.readLock();

        rLock.lock();//判断一下是否有这把锁，有的话再判断一下模式是否read,是的话直接往下走，读读之间兼容
        System.out.println("获取读锁成功： clientId=" + clientId);

        String stock = stringRedisTemplate.opsForValue().get("stock");
        if (StringUtils.isEmpty(stock)) {
            System.out.println("查询数据库库存为10...");
            Thread.sleep(5000);
            stringRedisTemplate.opsForValue().set("stock", "10");
        }
        rLock.unlock();
        System.out.println("释放读锁成功：client= " + clientId);
        return "end";
    }


    @RequestMapping("/update_stock")
    public String updateStock(@RequestParam("clientId") Long clientId) throws Exception {
        String lockKey = "product_stock_101";

        RReadWriteLock readWriteLock = redisson.getReadWriteLock(lockKey);
        RLock writeLock = readWriteLock.writeLock();

        writeLock.lock();//没有锁的情况下才能获取成功
        System.out.println("获取写锁成功，clientId=" + clientId);
        System.out.println("修改商品101的数据库为5...");
        stringRedisTemplate.delete("stock");
        Thread.sleep(5000);
        writeLock.unlock();
        System.out.println("释放写锁成功： clientId = " + clientId);

        return "end";
    }


}