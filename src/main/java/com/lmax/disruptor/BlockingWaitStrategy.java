/*
 * Copyright 2011 LMAX Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lmax.disruptor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lmax.disruptor.util.ThreadHints;

/**
 * Blocking strategy that uses a lock and condition variable for {@link EventProcessor}s waiting on a barrier.
 * <p>
 * This strategy can be used when throughput and low-latency are not as important as CPU resource.
 */
public final class BlockingWaitStrategy implements WaitStrategy
{
    private final Lock lock = new ReentrantLock();
    private final Condition processorNotifyCondition = lock.newCondition();

    @Override
    public long waitFor(long sequence, Sequence cursorSequence, Sequence dependentSequence, SequenceBarrier barrier)
        throws AlertException, InterruptedException {
        long availableSequence;
        // 生产者的生产偏移量 小于 我的消费偏移量
        if (cursorSequence.get() < sequence) {
            lock.lock();
            try {
                while (cursorSequence.get() < sequence) {
                    // 检查当前的线程是否被停止，是则抛出异常 AlertException
                    barrier.checkAlert();
                    // 阻塞等待
                    processorNotifyCondition.await();
                }
            } finally {
                lock.unlock();
            }
        }
        // 确保该序号已经被我前面的消费者消费(协调与其他消费者的关系)
        while ((availableSequence = dependentSequence.get()) < sequence) {
            barrier.checkAlert();
            // 自旋等待
            ThreadHints.onSpinWait();
        }

        return availableSequence;
    }

    @Override
    public void signalAllWhenBlocking() {
        lock.lock();
        try {
            // 唤醒当前阻塞的线程
            processorNotifyCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return "BlockingWaitStrategy{" +
            "processorNotifyCondition=" + processorNotifyCondition +
            '}';
    }
}
