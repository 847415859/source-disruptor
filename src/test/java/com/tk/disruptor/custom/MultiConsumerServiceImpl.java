package com.tk.disruptor.custom;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * @Description: 100个事件，三个消费者，每个都独自消费这个100个事件
 * @Date : 2023/11/24 9:36
 * @Auther : tiankun
 */
@Slf4j
public class MultiConsumerServiceImpl implements LowLevelOperateService{
    private static RingBuffer<StringEvent> ringBuffer;

    private static StringEventProducer producer;

    /**
     * 统计消息总数
     */
    private static final AtomicLong eventCount = new AtomicLong();

    private ExecutorService executors;


    public static StringEventProducer getProducerInstance(){
        if(producer == null) {
            synchronized (OneConsumerServiceImpl.class) {
                if (producer == null) {
                    Consumer<StringEvent> consumerSupplier = event -> {
                        long count = eventCount.incrementAndGet();
                        log.info("receive [{}] event", count);
                    };

                    // 创建环形队列实例
                    ringBuffer = RingBuffer.createSingleProducer(new StringEventFactory(), BUFFER_SIZE);
                    // 创建线程池
                    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(CONSUMER_NUM);
                    executor.prestartAllCoreThreads();
                    for (int i = 0; i < CONSUMER_NUM; i++) {
                        addProcessor(consumerSupplier, executor);
                    }
                    // 生产者
                    producer = new StringEventProducer(ringBuffer);
                }
            }
        }
        return producer;
    }

    /**
     * 生产一个BatchEventProcessor实例，并且启动独立线程开始获取和消费消息
     * @param consumerSupplier
     * @param executor
     */
    private static void addProcessor(Consumer<StringEvent> consumerSupplier, ExecutorService executor) {
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
        // 创建事件处理的工作类，里面执行StringEventHandler处理事件
        BatchEventProcessor<StringEvent> eventProcessor = new BatchEventProcessor<>(ringBuffer, sequenceBarrier,
                new StringEventHandler(consumerSupplier));

        // 将消费者的sequence传给环形队列
        ringBuffer.addGatingSequences(eventProcessor.getSequence());
        // 在一个独立线程中取事件并消费
        executor.submit(eventProcessor);
    }


    @Override
    public void publish(String content) {
        producer.onData(content);
    }

    @Override
    public long eventCount() {
        return eventCount.get();
    }


    public static void main(String[] args) {
        StringEventProducer producerInstance = MultiConsumerServiceImpl.getProducerInstance();
        for (int i = 1; i <= 100; i++) {
            producerInstance.onData("乾坤"+i);
        }
    }

}
