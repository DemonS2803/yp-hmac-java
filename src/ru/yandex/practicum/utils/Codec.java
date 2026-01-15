package ru.yandex.practicum.utils;

import java.util.Base64;

public class Codec {

    public static String toBase64(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    public static byte[] fromBase64(String encodedData) {
        return Base64.getUrlDecoder().decode(encodedData);
    }

    public static String stringFromBase64(String encodedData) {
        return new String(fromBase64(encodedData));
    }

    public static boolean isBase64(String str) {
        if (str == null) {
            return false;
        }
        try {
            Base64.getUrlDecoder().decode(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
