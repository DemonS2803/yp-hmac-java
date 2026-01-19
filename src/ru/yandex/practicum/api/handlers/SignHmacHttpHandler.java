package ru.yandex.practicum.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.api.dto.SignRequestDto;
import ru.yandex.practicum.api.dto.SignResponseDto;
import ru.yandex.practicum.services.HmacService;
import ru.yandex.practicum.utils.Config;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class SignHmacHttpHandler extends BaseHttpHandler {
    private static final Logger log = Logger.getLogger(SignHmacHttpHandler.class.getName());

    public SignHmacHttpHandler(HmacService service, Config config, Gson gson) {
        super(service, config, gson);
    }

    @Override
    protected void handlePost(HttpExchange httpExchange)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        SignRequestDto dto = gson.fromJson(getRequestBody(httpExchange), SignRequestDto.class);
        log.info(STR."Sign message with size \{dto.getMsg().length()}");
        String signature = service.sign(dto.getMsg());

        log.info("Signature for message has been done");
        SignResponseDto responseDto = new SignResponseDto(signature);
        sendSuccess(httpExchange, gson.toJson(responseDto));
    }

}
