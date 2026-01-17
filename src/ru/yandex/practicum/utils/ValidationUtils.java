package ru.yandex.practicum.utils;

import ru.yandex.practicum.exceptions.InvalidSignatureFormatException;
import ru.yandex.practicum.exceptions.MessageIsEmptyException;
import ru.yandex.practicum.exceptions.MessageTooLargeException;

import java.util.logging.Logger;

public class ValidationUtils {

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
        if (msg.getBytes().length > maxMsgSizeSizeBytes) {
            throw new MessageTooLargeException(
                    STR."Message is too large: \{msg.getBytes().length}. Only \{maxMsgSizeSizeBytes} available"
            );
        }
    }


}
