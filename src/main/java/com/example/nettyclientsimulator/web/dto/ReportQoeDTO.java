package com.example.nettyclientsimulator.web.dto;


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
public class ReportQoeDTO {
    private String clientId;
    private Long delay;
    private Boolean enable;
}
