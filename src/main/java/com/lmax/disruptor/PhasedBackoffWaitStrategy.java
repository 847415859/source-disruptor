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

import java.util.concurrent.TimeUnit;

/**
 * 分阶段性的等待策略，在不同的等阶阶段采用不同的方式等待。
 *
 * <p>Phased wait strategy for waiting {@link EventProcessor}s on a barrier.</p>
 *
 * <p>This strategy can be used when throughput and low-latency are not as important as CPU resource.
 * Spins, then yields, then waits using the configured fallback WaitStrategy.</p>
 */
public final class PhasedBackoffWaitStrategy implements WaitStrategy
{
    // 自旋重试的次数
    private static final int SPIN_TRIES = 10000;
    // 自旋超时时间
    private final long spinTimeoutNanos;
    // yield 的超时时间
    private final long yieldTimeoutNanos;
    // 保底策略
    private final WaitStrategy fallbackStrategy;

    public PhasedBackoffWaitStrategy(
        long spinTimeout,
        long yieldTimeout,
        TimeUnit units,
        WaitStrategy fallbackStrategy)
    {
        this.spinTimeoutNanos = units.toNanos(spinTimeout);
        this.yieldTimeoutNanos = spinTimeoutNanos + units.toNanos(yieldTimeout);
        this.fallbackStrategy = fallbackStrategy;
    }

    /**
     * Construct {@link PhasedBackoffWaitStrategy} with fallback to {@link BlockingWaitStrategy}
     *
     * @param spinTimeout The maximum time in to busy spin for.
     * @param yieldTimeout The maximum time in to yield for.
     * @param units Time units used for the timeout values.
     * @return The constructed wait strategy.
     */
    public static PhasedBackoffWaitStrategy withLock(
        long spinTimeout,
        long yieldTimeout,
        TimeUnit units)
    {
        return new PhasedBackoffWaitStrategy(
            spinTimeout, yieldTimeout,
            units, new BlockingWaitStrategy());
    }

    /**
     * Construct {@link PhasedBackoffWaitStrategy} with fallback to {@link LiteBlockingWaitStrategy}
     *
     * @param spinTimeout The maximum time in to busy spin for.
     * @param yieldTimeout The maximum time in to yield for.
     * @param units Time units used for the timeout values.
     * @return The constructed wait strategy.
     */
    public static PhasedBackoffWaitStrategy withLiteLock(
        long spinTimeout,
        long yieldTimeout,
        TimeUnit units)
    {
        return new PhasedBackoffWaitStrategy(
            spinTimeout, yieldTimeout,
            units, new LiteBlockingWaitStrategy());
    }

    /**
     * Construct {@link PhasedBackoffWaitStrategy} with fallback to {@link SleepingWaitStrategy}
     *
     * @param spinTimeout The maximum time in to busy spin for.
     * @param yieldTimeout The maximum time in to yield for.
     * @param units Time units used for the timeout values.
     * @return The constructed wait strategy.
     */
    public static PhasedBackoffWaitStrategy withSleep(
        long spinTimeout,
        long yieldTimeout,
        TimeUnit units)
    {
        return new PhasedBackoffWaitStrategy(
            spinTimeout, yieldTimeout,
            units, new SleepingWaitStrategy(0));
    }

    @Override
    public long waitFor(long sequence, Sequence cursor, Sequence dependentSequence, SequenceBarrier barrier)
        throws AlertException, InterruptedException, TimeoutException
    {
        long availableSequence;
        long startTime = 0;
        int counter = SPIN_TRIES;

        do
        {
            if ((availableSequence = dependentSequence.get()) >= sequence)
            {
                return availableSequence;
            }
            // 先自旋
            if (0 == --counter) {
                if (0 == startTime) {
                    startTime = System.nanoTime();
                } else {
                    long timeDelta = System.nanoTime() - startTime;
                    // yield 超时
                    if (timeDelta > yieldTimeoutNanos) {
                        return fallbackStrategy.waitFor(sequence, cursor, dependentSequence, barrier);
                    // 自旋超时
                    } else if (timeDelta > spinTimeoutNanos) {
                        Thread.yield();
                    }
                }
                counter = SPIN_TRIES;
            }
        }
        while (true);
    }

    @Override
    public void signalAllWhenBlocking()
    {
        fallbackStrategy.signalAllWhenBlocking();
    }
}
