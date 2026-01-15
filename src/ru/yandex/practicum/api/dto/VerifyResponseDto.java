package ru.yandex.practicum.api.dto;

public class VerifyResponseDto {

    private String ok;

    public VerifyResponseDto(Boolean isOk) {
        ok = isOk ? "true" : "false";
    }

    public String getOk() {
        return ok;
    }
}
