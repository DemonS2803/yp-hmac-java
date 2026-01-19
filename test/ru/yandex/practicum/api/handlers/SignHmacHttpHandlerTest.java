package ru.yandex.practicum.api.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.api.dto.SignRequestDto;
import ru.yandex.practicum.api.dto.SignResponseDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SignHmacHttpHandlerTest extends BaseHttpHandlerTest {

    @ParameterizedTest
    @ValueSource(strings = {"Hello", "data", "test123"})
    void testSignEndpoint_validDeterminedResult(String msg) throws IOException, InterruptedException {
        SignRequestDto requestDto = new SignRequestDto(msg);
        HttpResponse<String> response = sendRequest(server.getGson().toJson(requestDto), "POST", "/sign");
        HttpResponse<String> response2 = sendRequest(server.getGson().toJson(requestDto), "POST", "/sign");
        SignResponseDto dto = server.getGson().fromJson(response.body(), SignResponseDto.class);
        SignResponseDto dto2 = server.getGson().fromJson(response2.body(), SignResponseDto.class);
        assertEquals(dto.getSignature(), dto2.getSignature(), "Signatures must be equals for same msg and secret");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Hello", "A", "toolargeincomingmessage", "anotherlarge"})
    void testSignEndpoint_tooLargeMsg(String msg) throws IOException, InterruptedException {
        SignRequestDto requestDto = new SignRequestDto(msg);
        HttpResponse<String> response = sendRequest(server.getGson().toJson(requestDto), "POST", "/sign");
        if (msg.length() > 10) {
            assertEquals(413, response.statusCode());
        } else {
            assertEquals(200, response.statusCode());
        }
    }

    @Test
    void testSignEndpoint_emptyMsg() throws IOException, InterruptedException {
        SignRequestDto requestDto = new SignRequestDto("");
        HttpResponse<String> response = sendRequest(server.getGson().toJson(requestDto), "POST", "/sign");
        assertEquals(400, response.statusCode());
    }

    @Test
    void testSignEndpoint_unappropriateMediaContent() throws IOException, InterruptedException {
        SignRequestDto requestDto = new SignRequestDto("Hello");
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create(STR."http://localhost:\{config.getListenPort()}/sign");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .method("POST", HttpRequest.BodyPublishers.ofString(server.getGson().toJson(requestDto)))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(415, response.statusCode());

        }
    }

}
