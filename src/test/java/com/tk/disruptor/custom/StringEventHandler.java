package com.tk.disruptor.custom;

import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * @Description:
 * @Date : 2023/11/23 19:24
 * @Auther : tiankun
 */
@Slf4j
public class StringEventHandler implements EventHandler<StringEvent> {

    private Consumer<StringEvent> consumer;

    public StringEventHandler(Consumer<StringEvent> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onEvent(StringEvent event, long sequence, boolean endOfBatch) throws Exception {
        log.info("sequence [{}], endOfBatch [{}], event : {}", sequence, endOfBatch, event);

        // 这里延时100ms，模拟消费事件的逻辑的耗时
        Thread.sleep(100);

        // 如果外部传入了consumer，就要执行一次accept方法
        if (null!=consumer) {
            consumer.accept(event);
        }
    }
}
