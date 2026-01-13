package ru.yandex.practicum.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.api.dto.VerifyRequestDto;
import ru.yandex.practicum.api.dto.VerifyResponseDto;
import ru.yandex.practicum.services.HmacService;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class VerifyHmacHttpHandler extends BaseHttpHandler {
    private static final Logger log = Logger.getLogger(VerifyHmacHttpHandler.class.getName());

    public VerifyHmacHttpHandler(HmacService service, Gson gson) {
        super(service, gson);
    }

    @Override
    protected void handleGet(HttpExchange httpExchange) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        log.info("wanna verify msg");
        VerifyRequestDto dto = gson.fromJson(getRequestBody(httpExchange), VerifyRequestDto.class);
        boolean isValid = service.verify(dto.getMsg(), dto.getSignature());

        log.info(STR."Message <\{dto.getMsg()}> with signature <\{dto.getSignature()} is valid: \{isValid}");
        VerifyResponseDto responseDto = new VerifyResponseDto(isValid);
        sendSuccess(httpExchange, gson.toJson(responseDto));
    }
}
