package com.tk.disruptor.custom.consumer;

/**
 * @Description: 100个订单，短信系统独立消费，与此同时，两个邮件服务器共同消费
 * @Date : 2023/11/24 11:51
 * @Auther : tiankun
 */
public class IndependentAndShareModeServiceImpl extends ConsumeModeService{
    @Override
    protected void disruptorOperate() {
        // 调用handleEventsWith，表示创建的多个消费者，每个都是独立消费的
        // 这里创建一个消费者，短信服务
        disruptor.handleEventsWith(new SmsEventHandler(consumer));

        // mailWorkHandler1模拟一号邮件服务器
        MailWorkHandler mailWorkHandler1 = new MailWorkHandler(consumer);
        // mailWorkHandler2模拟一号邮件服务器
        MailWorkHandler mailWorkHandler2 = new MailWorkHandler(consumer);
        // 调用handleEventsWithWorkerPool，表示创建的多个消费者以共同消费的模式消费
        disruptor.handleEventsWithWorkerPool(mailWorkHandler1, mailWorkHandler2);
    }

    public static void main(String[] args) {
        IndependentAndShareModeServiceImpl service = new IndependentAndShareModeServiceImpl();
        for (int i = 1; i <= 100; i++) {
            service.publish("乾坤"+i);
        }
    }
}
