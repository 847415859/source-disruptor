package com.tk.disruptor;

import com.lmax.disruptor.RingBuffer;

/**
 * @Description:
 * @Date : 2023/05/13 16:59
 * @Auther : tiankun
 */
public class OrderEventProducer {
    //事件队列
    private RingBuffer<OrderEvent> ringBuffer;

    public OrderEventProducer(RingBuffer<OrderEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(long value,String name) {
        // 获取事件队列 的下一个槽
        long sequence = ringBuffer.next();
        try {
            //获取消息（事件）
            OrderEvent orderEvent = ringBuffer.get(sequence);
            // 写入消息数据
            orderEvent.setValue(value);
            orderEvent.setName(name);
        } catch (Exception e) {
            // TODO  异常处理
            e.printStackTrace();
        } finally {
            System.out.println("生产者发送数据value:"+value+",name:"+name);
            //发布事件,标识当前进度的事件可用
            ringBuffer.publish(sequence);
        }
    }
}
