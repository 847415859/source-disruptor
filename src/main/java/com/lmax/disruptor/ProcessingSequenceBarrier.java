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


/**
 * 消费者使用的事件处理器序号屏障
 * {@link SequenceBarrier} handed out for gating {@link EventProcessor}s on a cursor sequence and optional dependent {@link EventProcessor}(s),
 * using the given WaitStrategy.
 */
final class ProcessingSequenceBarrier implements SequenceBarrier {
    /**
     * 消费者等待策略
     */
    private final WaitStrategy waitStrategy;
    /**
     * 依赖的Sequence。
     * EventProcessor(事件处理器)的Sequence必须小于等于依赖的Sequence
     * 来自于{@link com.lmax.disruptor.dsl.EventHandlerGroup#sequences}
     *       当使用  {@link com.lmax.disruptor.dsl.EventHandlerGroup#then}
     *       then开头构建消费者关系的时候将上一层消费者
     * 对于直接和Sequencer相连的消费者，它依赖的Sequence就是Sequencer的Sequence。
     * 对于跟在其它消费者屁股后面的消费者，它依赖的Sequence就是它跟随的所有消费者的Sequence。
     *
     * 类似 {@link AbstractSequencer#gatingSequences}
     * dependentSequence
     */
    private final Sequence dependentSequence;
    /**
     * 是否请求了关闭消费者
     */
    private volatile boolean alerted = false;
    /**
     * 生产者的进度(cursor)
     * 依赖该屏障的事件处理器的进度【必然要小于等于】生产者的进度
     */
    private final Sequence cursorSequence;
    /**
     * 序号生成器(来自生产者)
     */
    private final Sequencer sequencer;

    ProcessingSequenceBarrier(
        final Sequencer sequencer,
        final WaitStrategy waitStrategy,
        final Sequence cursorSequence,
        final Sequence[] dependentSequences)
    {
        this.sequencer = sequencer;
        this.waitStrategy = waitStrategy;
        this.cursorSequence = cursorSequence;
        if (0 == dependentSequences.length)
        {
            dependentSequence = cursorSequence;
        }
        else
        {
            dependentSequence = new FixedSequenceGroup(dependentSequences);
        }
    }

    @Override
    public long waitFor(final long sequence)
        throws AlertException, InterruptedException, TimeoutException
    {
        checkAlert();
        // 根据等待策略获取可用的进度
        long availableSequence = waitStrategy.waitFor(sequence, cursorSequence, dependentSequence, this);

        if (availableSequence < sequence)
        {
            return availableSequence;
        }

        return sequencer.getHighestPublishedSequence(sequence, availableSequence);
    }

    @Override
    public long getCursor()
    {
        return dependentSequence.get();
    }

    @Override
    public boolean isAlerted()
    {
        return alerted;
    }

    @Override
    public void alert()
    {
        alerted = true;
        waitStrategy.signalAllWhenBlocking();
    }

    @Override
    public void clearAlert()
    {
        alerted = false;
    }

    @Override
    public void checkAlert() throws AlertException
    {
        if (alerted) {
            throw AlertException.INSTANCE;
        }
    }
}