package com.tk.disruptor.custom.consumer;

import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @Description: 独立消费的短信服务
 * @Date : 2023/11/24 10:58
 * @Auther : tiankun
 */
@Slf4j
public class SmsEventHandler implements EventHandler<OrderEvent> {

    private Consumer<OrderEvent> consumer;

    public SmsEventHandler(Consumer<OrderEvent> consumer) {
        this.consumer = consumer;
    }

    public SmsEventHandler() {
    }

    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {
        log.info("短信服务 sequence [{}], endOfBatch [{}], event : {}", sequence, endOfBatch, event);

        // 这里延时100ms，模拟消费事件的逻辑的耗时
        Thread.sleep(100);

        // 如果外部传入了consumer，就要执行一次accept方法
        if (consumer != null) {
            consumer.accept(null);
        }
    }
}
