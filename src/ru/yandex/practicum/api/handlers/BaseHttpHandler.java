package ru.yandex.practicum.api.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.api.dto.ErrorResponse;
import ru.yandex.practicum.api.utils.HttpConstants;
import ru.yandex.practicum.exceptions.InvalidSignatureFormatException;
import ru.yandex.practicum.exceptions.MessageIsEmptyException;
import ru.yandex.practicum.exceptions.MessageTooLargeException;
import ru.yandex.practicum.exceptions.RequestBodyTooLargeException;
import ru.yandex.practicum.exceptions.UnsupportedMediaTypeException;
import ru.yandex.practicum.services.HmacService;
import ru.yandex.practicum.utils.Config;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

public class BaseHttpHandler implements HttpHandler {
    private static final Logger log = Logger.getLogger(BaseHttpHandler.class.getName());

    HmacService service;
    Config config;
    Gson gson;

    public BaseHttpHandler(HmacService service, Config config, Gson gson) {
        this.service = service;
        this.config = config;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            log.info(String.format("%s: %s %s", LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                    method, httpExchange.getRequestURI()));
            validateRequest(httpExchange);

            switch (method) {
                case "GET": {
                    handleGet(httpExchange);
                    break;
                }
                case "POST": {
                    handlePost(httpExchange);
                    break;
                }
                default: {
                    sendMethodNotSupported(httpExchange);
                }
            }
        } catch (MessageTooLargeException e) {
            sendMessageTooLarge(httpExchange);
        } catch (RequestBodyTooLargeException e) {
            sendMessageTooLarge(httpExchange);
        } catch (MessageIsEmptyException e) {
            sendMessageIsEmpty(httpExchange);
        } catch (InvalidSignatureFormatException e) {
            sendInvalidSignatureFormat(httpExchange);
        } catch (JsonSyntaxException e) {
            sendInvalidJson(httpExchange);
        } catch (UnsupportedMediaTypeException e) {
            sendUnsupportedMediaType(httpExchange);
        } catch (Exception e) {
            log.severe(STR."Unhandled exception: \{e.getMessage()}");
            e.printStackTrace();
            sendError(httpExchange);
        }
    }

    protected void validateRequest(HttpExchange httpExchange) throws IOException {
        validateMediaType(httpExchange);
//        validateRequestSize(httpExchange);
//        log.info(STR."Content length: \{httpExchange.getRequestHeaders().get("Content-Length")}");
    }

    protected void validateMediaType(HttpExchange httpExchange) throws IOException {
        String contentType = String.valueOf(httpExchange.getRequestHeaders().get("Content-Type"));
        log.info(STR."http content type: \{contentType}");
        if (contentType != null && !contentType.contains("application/json")) {
            throw new UnsupportedMediaTypeException("Supports only application/json media type");
        }
    }

    protected void validateRequestSize(HttpExchange httpExchange) {
        int requestBodyLength = Integer.parseInt(httpExchange.getRequestHeaders().getFirst("Content-Length"));
        log.info(STR."Incoming content length: \{requestBodyLength}");
        if (requestBodyLength > config.getMaxRequestBodySizeBytes()) {
            throw new RequestBodyTooLargeException(STR."Message size is \{requestBodyLength}. Only 20 available");
        }
    }

    protected void handleGet(HttpExchange httpExchange)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        sendMethodNotSupported(httpExchange);
    }

    protected void handlePost(HttpExchange httpExchange) throws IOException {
        sendMethodNotSupported(httpExchange);
    }

    protected void sendSuccess(HttpExchange httpExchange, String text) throws IOException {
        sendText(httpExchange, 200, text);
    }

    protected void sendText(HttpExchange httpExchange, int status, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            outputStream.write(bytes);
        }
    }

    protected void sendMethodNotSupported(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, 405, createErrorJsonResponse(HttpConstants.REQUESTED_METHOD_NOT_SUPPORTED_ERROR));
    }

    protected void sendMessageTooLarge(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, 400, createErrorJsonResponse(HttpConstants.INVALID_MESSAGE_ERROR));
    }
    protected void sendRequestBodyTooLarge(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, 413, createErrorJsonResponse(HttpConstants.REQUEST_BODY_TOO_LARGE_ERROR));
    }

    protected void sendUnsupportedMediaType(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, 415, createErrorJsonResponse(HttpConstants.UNSUPPORTED_MEDIA_TYPE_ERROR));
    }

    protected void sendInvalidJson(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, 400, createErrorJsonResponse(HttpConstants.INVALID_JSON_ERROR));
    }

    protected void sendMessageIsEmpty(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, 400, createErrorJsonResponse(HttpConstants.MESSAGE_IS_EMPTY_ERROR));
    }

    protected void sendInvalidSignatureFormat(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, 400, createErrorJsonResponse(HttpConstants.INVALID_SIGNATURE_FORMAT_ERROR));
    }

    protected void sendError(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, 500, createErrorJsonResponse(HttpConstants.INTERNAL_SERVER_ERROR));
    }

    protected String getRequestBody(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private String createErrorJsonResponse(String error) {
        return gson.toJson(new ErrorResponse(error));
    }

}
