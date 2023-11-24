package com.tk.disruptor.custom.consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Date : 2023/11/24 10:52
 * @Auther : tiankun
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent {
    String value;
}
