package com.tk.disruptor.custom.consumer;

import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * @Description: 邮件服务 独立消费
 * @Date : 2023/11/24 11:00
 * @Auther : tiankun
 */
@Slf4j
public class MailEventHandler implements EventHandler<OrderEvent> {
    private Consumer<OrderEvent> consumer;

    public MailEventHandler(Consumer<OrderEvent> consumer) {
        this.consumer = consumer;
    }

    public MailEventHandler() {
    }

    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {
        log.info("邮件服务 sequence [{}], endOfBatch [{}], event : {}", sequence, endOfBatch, event);

        // 这里延时100ms，模拟消费事件的逻辑的耗时
        Thread.sleep(100);

        // 如果外部传入了consumer，就要执行一次accept方法
        if (consumer != null) {
            consumer.accept(null);
        }
    }
}
