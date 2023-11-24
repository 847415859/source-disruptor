package com.tk.disruptor.custom.consumer;

import com.lmax.disruptor.EventFactory;

/**
 * @Description:
 * @Date : 2023/11/24 10:53
 * @Auther : tiankun
 */
public class OrderEventFactory implements EventFactory<OrderEvent> {
    @Override
    public OrderEvent newInstance() {
        return new OrderEvent();
    }
}
