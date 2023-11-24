package com.tk.disruptor.custom.consumer;

/**
 * @Description: 100个订单，短信和邮件系统独立消费
 * @Date : 2023/11/24 11:25
 * @Auther : tiankun
 */
public class IndependentModeServiceImpl extends ConsumeModeService{
    @Override
    protected void disruptorOperate() {
        disruptor.handleEventsWith(new MailEventHandler(consumer),new SmsEventHandler(consumer));
    }

    public static void main(String[] args) {
        IndependentModeServiceImpl service = new IndependentModeServiceImpl();
        for (int i = 1; i <= 100; i++) {
            service.publish("乾坤"+i);
        }
    }
}
