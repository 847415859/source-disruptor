package com.tk.disruptor.custom.consumer;

import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * @Description: 邮件服务-共同消费
 * @Date : 2023/11/24 11:03
 * @Auther : tiankun
 */
@Slf4j
public class MailWorkHandler implements WorkHandler<OrderEvent> {

    private Consumer<OrderEvent> consumer;

    public MailWorkHandler(Consumer<OrderEvent> consumer) {
        this.consumer = consumer;
    }

    public MailWorkHandler() {
    }


    @Override
    public void onEvent(OrderEvent event) throws Exception {
        log.info("共同消费邮件服务 event : {}", event);

        // 这里延时100ms，模拟消费事件的逻辑的耗时
        Thread.sleep(100);

        // 如果外部传入了consumer，就要执行一次accept方法
        if (consumer != null) {
            consumer.accept(null);
        }
    }
}
