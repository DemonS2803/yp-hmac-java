package ru.yandex.practicum.utils;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.exceptions.InvalidSignatureFormatException;
import ru.yandex.practicum.exceptions.MessageIsEmptyException;
import ru.yandex.practicum.exceptions.MessageTooLargeException;
import ru.yandex.practicum.exceptions.RequestBodyTooLargeException;
import ru.yandex.practicum.exceptions.UnsupportedMediaTypeException;

import java.io.IOException;
import java.util.logging.Logger;

public class ValidationUtils {
    private static final Logger log = Logger.getLogger(ValidationUtils.class.getName());

    public static void validateSignatureIsBase64(String signature) {
        if (!Codec.isBase64(signature)) {
            throw new InvalidSignatureFormatException("Signature format is not base64");
        }
    }

    public static void validateMsg(String msg, Config config) {
        validateMsgIsEmpty(msg);
        validateMsgMaxSize(msg, config.getMaxMsgSizeBytes());
    }

    public static void validateMsgIsEmpty(String msg) {
        if (msg == null || msg.isBlank()) {
            throw new MessageIsEmptyException("Message is empty or not string");
        }
    }

    public static void validateMsgMaxSize(String msg, int maxMsgSizeSizeBytes) {
        System.out.println(STR."msg size: \{msg.getBytes().length}, available: \{maxMsgSizeSizeBytes}");
        if (msg.getBytes().length > maxMsgSizeSizeBytes) {
            throw new MessageTooLargeException(
                    STR."Message is too large: \{msg.getBytes().length}. Only \{maxMsgSizeSizeBytes} available"
            );
        }
    }

    public static void validateRequest(HttpExchange httpExchange, Config config) throws IOException {
        validateMediaType(httpExchange);
        validateRequestSize(httpExchange, config.getMaxMsgSizeBytes());
    }

    public static void validateMediaType(HttpExchange httpExchange) throws IOException {
        String contentType = String.valueOf(httpExchange.getRequestHeaders().get("Content-Type"));
        log.fine(STR."http content type: \{contentType}");
        if (contentType != null && !contentType.contains("application/json")) {
            throw new UnsupportedMediaTypeException("Supports only application/json media type");
        }
    }

    public static void validateRequestSize(HttpExchange httpExchange, int maxRequestSize) {
        int requestBodyLength = Integer.parseInt(httpExchange.getRequestHeaders().getFirst("Content-Length"));
        log.fine(STR."Incoming content length: \{requestBodyLength}. Max available: \{maxRequestSize}");
        if (requestBodyLength > maxRequestSize) {
            throw new RequestBodyTooLargeException(STR."Message size is \{requestBodyLength}. Only 20 available");
        }
    }

}
