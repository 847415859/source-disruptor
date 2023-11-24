package com.lmax.disruptor;

/**
 * 批量事件处理通知(感知)
 * 如果你的EventHandler实现了该接口，那么当批处理开始时，你会收到通知
 */
public interface BatchStartAware
{
    void onBatchStart(long batchSize);
}
