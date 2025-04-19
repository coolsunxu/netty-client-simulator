package com.example.nettyclientsimulator.web.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sunxu
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportQoeVO {
    private String clientId;
    private Long delay;
    private Boolean enable;
}
