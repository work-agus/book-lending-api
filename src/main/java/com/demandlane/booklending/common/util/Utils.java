package com.demandlane.booklending.common.util;

import com.demandlane.booklending.common.dto.ResponseDto;

public class Utils {
    public static <T> ResponseDto<T> getResponse(T data) {
        return new ResponseDto<>(200, "Success", System.currentTimeMillis(), data);
    }
}
