package com.example.odyssey.fileandserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EfmResponse<T> {
    private boolean success;
    private String errorCode;
    private String errorMessage;
    private T data;

    public static <T> EfmResponse<T> success(T data) {
        return EfmResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> EfmResponse<T> error(String errorCode, String errorMessage) {
        return EfmResponse.<T>builder()
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }
}
