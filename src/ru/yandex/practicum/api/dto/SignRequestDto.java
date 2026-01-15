package ru.yandex.practicum.api.dto;

public class SignRequestDto {

    private String msg;

    public SignRequestDto(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
