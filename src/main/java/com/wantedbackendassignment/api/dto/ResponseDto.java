package com.wantedbackendassignment.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseDto<T> {

    private int responseCode;
    private T data;
    private T error;

    public static <T> ResponseDto<T> success(T data, int responseCode) {
        ResponseDto<T> responseDto = new ResponseDto<>();
        responseDto.responseCode = responseCode;
        responseDto.data = data;
        return responseDto;
    }

    public static <T> ResponseDto<T> failure(T error, int responseCode) {
        ResponseDto<T> responseDto = new ResponseDto<>();
        responseDto.responseCode = responseCode;
        responseDto.error = error;
        return responseDto;
    }
}
