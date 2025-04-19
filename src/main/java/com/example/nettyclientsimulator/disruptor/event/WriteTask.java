package com.example.nettyclientsimulator.disruptor.event;


import lombok.*;

/**
 * @author sunxu
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class WriteTask {
    private Long id;

    private String clientId;

    private String str;

    private boolean finish;
}
