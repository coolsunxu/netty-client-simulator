package com.example.nettyclientsimulator.web.dto;

import lombok.*;


/**
 * @author sunxu
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {
    private Integer batch;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClientDTO{");
        sb.append("batch=").append(batch);
        sb.append('}');
        return sb.toString();
    }
}
