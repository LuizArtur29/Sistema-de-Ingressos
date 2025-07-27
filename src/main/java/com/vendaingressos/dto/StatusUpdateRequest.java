package com.vendaingressos.dto;

import lombok.Data;

@Data
public class StatusUpdateRequest {

    public String novoStatus;

    public StatusUpdateRequest() {}

    public StatusUpdateRequest(String novoStatus) {
        this.novoStatus = novoStatus;
    }
}