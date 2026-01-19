package ru.yandex.practicum.api.dto;

public class VerifyRequestDto {

    private String msg;
    private String signature;

    public VerifyRequestDto(String msg, String signature) {
        this.msg = msg;
        this.signature = signature;
    }

    public String getMsg() {
        return msg;
    }

    public String getSignature() {
        return signature;
    }
}
