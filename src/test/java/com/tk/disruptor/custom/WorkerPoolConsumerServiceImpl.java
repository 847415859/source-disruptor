package com.tk.disruptor.custom;

import com.lmax.disruptor.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * @Description: 100个事件，三个消费者共同消费这个100个事件
 * @Date : 2023/11/24 9:46
 * @Auther : tiankun
 */
@Slf4j
public class WorkerPoolConsumerServiceImpl implements LowLevelOperateService{
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
                    //  创建多个StringWorkHandler实例，放入一个数组中
                    WorkHandler<StringEvent>[] workHandlers = new WorkHandler[CONSUMER_NUM];
                    for (int i = 0; i < workHandlers.length; i++) {
                        workHandlers[i] = new StringWorkHandler(consumerSupplier);
                    }
                    // 创建WorkerPool实例，将StringWorkHandler实例的数组传进去，代表共同消费者的数量
                    WorkerPool<StringEvent> workerPool = new WorkerPool<>(ringBuffer,ringBuffer.newBarrier(),new IgnoreExceptionHandler(),
                            workHandlers);
                    // 一句很重要，去掉就会出现重复消费同一个事件的问题
                    ringBuffer.addGatingSequences(workerPool.getWorkerSequences());

                    workerPool.start(executor);
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
        StringEventProducer producerInstance = WorkerPoolConsumerServiceImpl.getProducerInstance();
        for (int i = 1; i <= 100; i++) {
            producerInstance.onData("乾坤"+i);
        }
    }
}
