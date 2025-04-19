package com.example.nettyclientsimulator.disruptor.element;

import lombok.*;

/**
 * @author sunxu
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReadElement extends BaseElement {
    private Object msg;
}
