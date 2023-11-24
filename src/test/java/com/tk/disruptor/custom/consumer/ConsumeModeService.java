package com.tk.disruptor.custom.consumer;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.Setter;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * @Description: 事件消费公共
 * @Date : 2023/11/24 11:07
 * @Auther : tiankun
 */
public abstract class ConsumeModeService {
    /**
     * 独立消费者数量
     */
    public static final int INDEPENDENT_CONSUMER_NUM = 2;

    /**
     * 环形缓冲区大小
     */
    protected int BUFFER_SIZE = 16;

    protected Disruptor<OrderEvent> disruptor;

    private OrderEventProducer producer;
    private OrderEventProducer producer2;

    /**
     * 统计消息总数
     */
    protected final AtomicLong eventCount = new AtomicLong();

    protected Consumer<OrderEvent> consumer = (orderEvent) -> {
        long count = eventCount.incrementAndGet();
    };


    // Init
    {
        disruptor = new Disruptor<OrderEvent>(
                new OrderEventFactory(),
                BUFFER_SIZE,
                Executors.newFixedThreadPool(3),
                ProducerType.MULTI,
                new YieldingWaitStrategy()
        );

        // 留给子类实现具体的事件消费逻辑
        disruptorOperate();

        disruptor.start();
        // 设置生产者
        producer = new OrderEventProducer(disruptor.getRingBuffer());
        // 创建第二个生产者
        producer2 = new OrderEventProducer(disruptor.getRingBuffer());

    }


    /**
     * 留给子类实现具体的事件消费逻辑
     */
    protected abstract void disruptorOperate();



    public void publish(String content) {
        producer.onData(content);
    }

    public long eventCount() {
        return eventCount.get();
    }
}
