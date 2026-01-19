package ru.yandex.practicum.api.utils;

public class HttpConstants {

    // base urls
    public static final String SIGN_URL = "/sign";
    public static final String VERIFY_URL = "/verify";


    // http errors
    public static final String INVALID_MESSAGE_ERROR = "invalid_message";
    public static final String INVALID_SIGNATURE_FORMAT_ERROR = "invalid_signature_format";
    public static final String INTERNAL_SERVER_ERROR = "internal_server_error";
    public static final String REQUESTED_METHOD_NOT_SUPPORTED_ERROR = "method_not_supported";
    public static final String UNSUPPORTED_MEDIA_TYPE_ERROR = "unsupported_media_type";
    public static final String INVALID_JSON_ERROR = "invalid_json";
    public static final String MESSAGE_IS_EMPTY_ERROR = "empty_message";
    public static final String REQUEST_BODY_TOO_LARGE_ERROR = "request_body_too_large";

}
