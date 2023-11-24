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
import java.util.function.Supplier;

/**
 * @Description:
 * @Date : 2023/11/23 19:40
 * @Auther : tiankun
 */
@Slf4j
public class OneConsumerServiceImpl implements LowLevelOperateService{

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
                        long count = eventCount.getAndIncrement();
                        log.info("receive [{}] event", count);
                    };

                    // 创建环形队列实例
                    ringBuffer = RingBuffer.createSingleProducer(new StringEventFactory(), BUFFER_SIZE);
                    // 创建线程池
                    ExecutorService executor = Executors.newSingleThreadExecutor();

                    SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
                    // 创建事件处理的工作类，里面执行StringEventHandler处理事件
                    BatchEventProcessor<StringEvent> eventProcessor = new BatchEventProcessor<>(ringBuffer, sequenceBarrier,
                            new StringEventHandler(consumerSupplier));

                    // 将消费者的sequence传给环形队列
                    ringBuffer.addGatingSequences(eventProcessor.getSequence());
                    // 在一个独立线程中取事件并消费
                    executor.submit(eventProcessor);
                    // 生产者
                    producer = new StringEventProducer(ringBuffer);
                }
            }
        }
        return producer;
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
        StringEventProducer producerInstance = OneConsumerServiceImpl.getProducerInstance();
        for (int i = 1; i <= 100; i++) {
            producerInstance.onData("乾坤"+i);
        }
    }
}
