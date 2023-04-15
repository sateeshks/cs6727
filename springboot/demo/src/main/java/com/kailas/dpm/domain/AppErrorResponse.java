package com.kailas.dpm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppErrorResponse {

    public AppErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    private String path;
    private Integer status;
    private String error;
    private String trace;

    private LocalDateTime timestamp;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
