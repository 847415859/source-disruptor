package com.tk.disruptor.custom.consumer;

import com.lmax.disruptor.RingBuffer;
import com.tk.disruptor.custom.StringEvent;

/**
 * @Description:
 * @Date : 2023/11/24 10:55
 * @Auther : tiankun
 */
public class OrderEventProducer {
    private RingBuffer<OrderEvent> ringBuffer;

    public OrderEventProducer(RingBuffer<OrderEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(String content){
        // ringBuffer是个队列，其next方法返回的是下最后一条记录之后的位置，这是个可用位置
        long next = ringBuffer.next();
        try {
            // sequence位置取出的事件是空事件
            OrderEvent stringEvent = ringBuffer.get(next);
            // 空事件添加业务信息
            stringEvent.setValue(content);
        } finally {
            // 发布事件
            ringBuffer.publish(next);
        }
    }
}
