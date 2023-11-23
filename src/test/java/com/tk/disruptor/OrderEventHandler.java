package com.tk.disruptor;

import com.lmax.disruptor.EventHandler;

/**
 * @Description:
 * @Date : 2023/05/13 17:00
 * @Auther : tiankun
 */
public class OrderEventHandler implements EventHandler<OrderEvent> {
    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {
        // TODO 消费逻辑
        System.out.println("消费者获取数据value:"+ event.getValue()+",name:"+event.getName());
    }
}
