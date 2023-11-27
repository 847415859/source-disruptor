package com.tk.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.LifecycleAware;
import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @Date : 2023/05/13 17:00
 * @Auther : tiankun
 */
@Slf4j
public class OrderEventHandler implements EventHandler<OrderEvent>, WorkHandler<OrderEvent>, LifecycleAware {
    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {
        // TODO 消费逻辑
        System.out.println("消费者获取数据value:"+ event.getValue()+",name:"+event.getName());
    }

    @Override
    public void onStart() {
        log.info("订单事件消费启动咯！");
    }

    @Override
    public void onShutdown() {
        log.warn("订单事件消费关闭咯！");
    }

    @Override
    public void onEvent(OrderEvent event) throws Exception {
        // TODO 消费逻辑
        System.out.println("[WorkHandler]消费者获取数据value:"+ event.getValue()+",name:"+event.getName());
    }
}
