package com.demandlane.booklending.common.util;

import com.demandlane.booklending.common.dto.ResponseDto;
import com.github.f4b6a3.uuid.UuidCreator;

import java.util.UUID;

public class Utils {
    public static <T> ResponseDto<T> getResponse(T data) {
        return new ResponseDto<>(200, "Success", System.currentTimeMillis(), data);
    }

    public static UUID getSystemUUID() {
        return UuidCreator.fromString(Constants.SYSTEM_UUID);
    }
}
