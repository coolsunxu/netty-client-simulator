package com.example.nettyclientsimulator.result;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sunxu
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OperateResult {
    private OperateStatus operateStatus;

}
