package com.example.nettyclientsimulator.web.vo;


import lombok.*;

/**
 * @author sunxu
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CloseConnectionVO {

    private String clientId;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CloseConnectionDTO{");
        sb.append("clientId='").append(clientId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
