package com.tk.disruptor.custom;

import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * @Description: 同组消费工作处理器
 * @Date : 2023/11/24 9:43
 * @Auther : tiankun
 */
@Slf4j
public class StringWorkHandler implements WorkHandler<StringEvent> {
    public StringWorkHandler(Consumer<StringEvent> consumer) {
        this.consumer = consumer;
    }

    // 外部可以传入Consumer实现类，每处理一条消息的时候，consumer的accept方法就会被执行一次
    private Consumer<StringEvent> consumer;

    @Override
    public void onEvent(StringEvent event) throws Exception {
        log.info("work handler event : {}", event);

        // 这里延时100ms，模拟消费事件的逻辑的耗时
        Thread.sleep(100);

        // 如果外部传入了consumer，就要执行一次accept方法
        if (null!=consumer) {
            consumer.accept(event);
        }
    }
}
