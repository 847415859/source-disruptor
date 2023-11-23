package com.tk.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * @Description:
 * @Date : 2023/05/13 16:58
 * @Auther : tiankun
 */
public class OrderEventFactory implements EventFactory<OrderEvent> {
    @Override
    public OrderEvent newInstance() {
        return new OrderEvent();
    }
}
