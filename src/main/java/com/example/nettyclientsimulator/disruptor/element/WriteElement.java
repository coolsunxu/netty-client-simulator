package com.example.nettyclientsimulator.disruptor.element;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author sunxu
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WriteElement extends BaseElement {
    private String str;
}
