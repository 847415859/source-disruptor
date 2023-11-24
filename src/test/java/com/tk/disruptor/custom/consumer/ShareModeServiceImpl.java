package com.tk.disruptor.custom.consumer;

/**
 * @Description:
 * @Date : 2023/11/24 11:45
 * @Auther : tiankun
 */
public class ShareModeServiceImpl extends ConsumeModeService{
    @Override
    protected void disruptorOperate() {
        // mailWorkHandler1模拟一号邮件服务器
        MailWorkHandler mailWorkHandler1 = new MailWorkHandler();
        // mailWorkHandler2模拟一号邮件服务器
        MailWorkHandler mailWorkHandler2 = new MailWorkHandler();

        // 调用handleEventsWithWorkerPool，表示创建的多个消费者以共同消费的模式消费
        disruptor.handleEventsWithWorkerPool(mailWorkHandler1, mailWorkHandler2);
    }

    public static void main(String[] args) {
        ShareModeServiceImpl service = new ShareModeServiceImpl();
        for (int i = 1; i <= 100; i++) {
            service.publish("乾坤"+i);
        }
    }
}
