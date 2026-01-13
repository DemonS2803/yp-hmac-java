package ru.yandex.practicum.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.api.dto.SignRequestDto;
import ru.yandex.practicum.api.dto.SignResponseDto;
import ru.yandex.practicum.services.HmacService;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class SignHmacHttpHandler extends BaseHttpHandler {
    private static final Logger log = Logger.getLogger(SignHmacHttpHandler.class.getName());

    public SignHmacHttpHandler(HmacService service, Gson gson) {
        super(service, gson);
    }

    @Override
    protected void handleGet(HttpExchange httpExchange)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        log.info("wanna sign for msg");
        SignRequestDto dto = gson.fromJson(getRequestBody(httpExchange), SignRequestDto.class);
        String signature = service.sign(dto.getMsg());

        log.info(STR."Signature for msg <\{dto.getMsg()}> has been done");
        SignResponseDto responseDto = new SignResponseDto(signature);
        sendSuccess(httpExchange, gson.toJson(responseDto));
    }

}
