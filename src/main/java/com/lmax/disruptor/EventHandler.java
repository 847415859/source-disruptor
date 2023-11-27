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
 * RingBuffer使用{@link #newInstance()}预创建对象预填充共享数据区。
 * 创建的实际是数据的载体对象，载体对象是反复使用的，会预分配，因此存在短时间的内存泄漏风险，
 * 因此讲道理最好每次处理完之后将数据进行清理，以帮助垃圾回收。
 *
 * Callback interface to be implemented for processing events as they become available in the {@link RingBuffer}
 *
 * @param <T> event implementation storing the data for sharing during exchange or parallel coordination of an event.
 * @see BatchEventProcessor#setExceptionHandler(ExceptionHandler) if you want to handle exceptions propagated out of the handler.
 */
public interface EventHandler<T>
{
    /**
     * Called when a publisher has published an event to the {@link RingBuffer}.  The {@link BatchEventProcessor} will
     * read messages from the {@link RingBuffer} in batches, where a batch is all of the events available to be
     * processed without having to wait for any new event to arrive.  This can be useful for event handlers that need
     * to do slower operations like I/O as they can group together the data from multiple events into a single
     * operation.  Implementations should ensure that the operation is always performed when endOfBatch is true as
     * the time between that message an the next one is inderminate.
     *
     * @param event      published to the {@link RingBuffer}
     *                   事件
     * @param sequence   of the event being processed
     *                   事件的进度
     * @param endOfBatch flag to indicate if this is the last event in a batch from the {@link RingBuffer}
     *                   标记，以指示这是否是批处理中的最后一个事件
     * @throws Exception if the EventHandler would like the exception handled further up the chain.
     */
    void onEvent(T event, long sequence, boolean endOfBatch) throws Exception;
}
