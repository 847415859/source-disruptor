package com.tk.disruptor.custom.consumer.scene;

import com.tk.disruptor.custom.consumer.ConsumeModeService;
import com.tk.disruptor.custom.consumer.IndependentModeServiceImpl;
import com.tk.disruptor.custom.consumer.MailEventHandler;

/**
 * @Description:
 * @Date : 2023/11/24 12:24
 * @Auther : tiankun
 */
public class Scene2 extends ConsumeModeService {
    @Override
    protected void disruptorOperate() {
        MailEventHandler c1 = new MailEventHandler(consumer);
        MailEventHandler c2 = new MailEventHandler(consumer);
        MailEventHandler c3 = new MailEventHandler(consumer);

        disruptor
                // C1、C2独立消费
                .handleEventsWith(c1, c2)
                // C3依赖C1和C2
                .then(c3);
    }

    public static void main(String[] args) {
        Scene2 service = new Scene2();
        for (int i = 1; i <= 10; i++) {
            service.publish("乾坤"+i);
        }
    }
}
