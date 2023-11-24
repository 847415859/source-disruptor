package com.tk.disruptor.custom;

import com.lmax.disruptor.EventFactory;

/**
 * @Description:
 * @Date : 2023/11/23 19:20
 * @Auther : tiankun
 */
public class StringEventFactory implements EventFactory<StringEvent> {
    @Override
    public StringEvent newInstance() {
        return new StringEvent();
    }
}
