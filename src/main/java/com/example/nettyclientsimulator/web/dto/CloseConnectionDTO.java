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
public class CloseConnectionDTO {
    private String clientId;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CloseConnectionDTO{");
        sb.append("clientId='").append(clientId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
