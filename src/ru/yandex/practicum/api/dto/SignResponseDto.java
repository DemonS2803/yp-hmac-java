package ru.yandex.practicum.api.dto;

public class SignResponseDto {

    private String signature;

    public SignResponseDto(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }
}
