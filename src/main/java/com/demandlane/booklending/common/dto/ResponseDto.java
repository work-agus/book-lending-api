package com.demandlane.booklending.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDto<T> {
    private int status;
    private String message;
    private long timestamp;
    private T data;
}