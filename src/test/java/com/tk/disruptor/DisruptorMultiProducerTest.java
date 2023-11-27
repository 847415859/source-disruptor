package com.tk.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executors;

/**
 * @Description: 多生产者测试
 * @Date : 2023/11/27 9:43
 * @Auther : tiankun
 */
public class DisruptorMultiProducerTest {

    public static void main(String[] args) {
        //创建disruptor
        Disruptor<OrderEvent> disruptor = new Disruptor<>(
                new OrderEventFactory(),
                1024 * 1024,
                Executors.defaultThreadFactory(),
                ProducerType.MULTI, //单生产者
                new BlockingWaitStrategy()  //等待策略
        );
        //设置消费者用于处理RingBuffer的事件
        disruptor.handleEventsWith(new OrderEventHandler())
                        .then(new OrderEventHandler());
        disruptor.start();

        //创建ringbuffer容器
        RingBuffer<OrderEvent> ringBuffer = disruptor.getRingBuffer();
        //创建生产者
        OrderEventProducer eventProducer = new OrderEventProducer(ringBuffer);
        //发送消息
        for(int i=0;i<5;i++){
            eventProducer.onData(i,"乾坤"+i);
        }

        disruptor.shutdown();
    }

}
