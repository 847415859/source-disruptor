package com.tk.disruptor.custom;

import com.lmax.disruptor.RingBuffer;

/**
 * @Description:
 * @Date : 2023/11/23 19:20
 * @Auther : tiankun
 */
public class StringEventProducer {

    private RingBuffer<StringEvent> ringBuffer;

    public StringEventProducer(RingBuffer<StringEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(String content){
        // ringBuffer是个队列，其next方法返回的是下最后一条记录之后的位置，这是个可用位置
        long next = ringBuffer.next();
        try {
            // sequence位置取出的事件是空事件
            StringEvent stringEvent = ringBuffer.get(next);
            // 空事件添加业务信息
            stringEvent.setValue(content);
        } finally {
            // 发布事件
            ringBuffer.publish(next);
        }

    }
}
