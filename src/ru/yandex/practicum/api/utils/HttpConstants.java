package ru.yandex.practicum.api.utils;

public class HttpConstants {

    // base urls
    public static final String SIGN_URL = "/sign";
    public static final String VERIFY_URL = "/verify";


    // http errors
    public static final String MESSAGE_TOO_LARGE_ERROR = "message_too_large";
    public static final String INVALID_SIGNATURE_FORMAT_ERROR = "invalid_signature_format";
    public static final String INTERNAL_SERVER_ERROR = "internal_server_error";
    public static final String REQUESTED_METHOD_NOT_SUPPORTED_ERROR = "method_not_supported";
    public static final String UNSUPPORTED_MEDIA_TYPE_ERROR = "unsupported_media_type";
    public static final String INVALID_JSON_ERROR = "invalid_json";
}
