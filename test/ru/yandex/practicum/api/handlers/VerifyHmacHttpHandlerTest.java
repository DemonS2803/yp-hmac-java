package ru.yandex.practicum.api.handlers;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.api.dto.SignRequestDto;
import ru.yandex.practicum.api.dto.SignResponseDto;
import ru.yandex.practicum.api.dto.VerifyRequestDto;
import ru.yandex.practicum.api.dto.VerifyResponseDto;
import ru.yandex.practicum.services.HmacService;
import ru.yandex.practicum.utils.Codec;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VerifyHmacHttpHandlerTest extends BaseHttpHandlerTest {

    @ParameterizedTest
    @ValueSource(strings = {"Hello", "a", "asdfasdfa"})
    void testVerifyEndpoint_successVerify(String msg) throws IOException, InterruptedException {
        SignRequestDto signRequestDto = new SignRequestDto(msg);
        HttpResponse<String> signReq = sendRequest(server.getGson().toJson(signRequestDto), "GET", "/sign");
        SignResponseDto dto = server.getGson().fromJson(signReq.body(), SignResponseDto.class);

        VerifyRequestDto verifyRequestDto = new VerifyRequestDto(msg, dto.getSignature());
        HttpResponse<String> verifyReq = sendRequest(server.getGson().toJson(verifyRequestDto), "GET", "/verify");
        VerifyResponseDto verifyResponseDto = server.getGson().fromJson(verifyReq.body(), VerifyResponseDto.class);
        assertEquals("true", verifyResponseDto.getOk());
    }

    @ParameterizedTest
    @MethodSource("incorrectVerifyMsgArgs")
    void testVerifyEndpoint_incorrectVerifyMsg(String signMsg, String verifyMsg) throws IOException, InterruptedException {
        SignRequestDto signRequestDto = new SignRequestDto(signMsg);
        HttpResponse<String> signReq = sendRequest(server.getGson().toJson(signRequestDto), "GET", "/sign");
        SignResponseDto dto = server.getGson().fromJson(signReq.body(), SignResponseDto.class);

        VerifyRequestDto verifyRequestDto = new VerifyRequestDto(verifyMsg, dto.getSignature());
        HttpResponse<String> verifyReq = sendRequest(server.getGson().toJson(verifyRequestDto), "GET", "/verify");
        VerifyResponseDto verifyResponseDto = server.getGson().fromJson(verifyReq.body(), VerifyResponseDto.class);
        assertEquals("false", verifyResponseDto.getOk());
    }

    static Stream<Arguments> incorrectVerifyMsgArgs() {
        return Stream.of(
                Arguments.of("Hello", "Hello!"),
                Arguments.of("a", "b"),
                Arguments.of("a", "A"),
                Arguments.of("asdf", "asdff")
        );
    }

    @Test
    void testVerifyEndpoint_brokenSignature_shouldFail() throws IOException, InterruptedException {
        String msg = "Hello";
        SignRequestDto signRequestDto = new SignRequestDto(msg);
        HttpResponse<String> signReq = sendRequest(server.getGson().toJson(signRequestDto), "GET", "/sign");
        SignResponseDto dto = server.getGson().fromJson(signReq.body(), SignResponseDto.class);

        VerifyRequestDto verifyRequestDto = new VerifyRequestDto(msg, "Invalid signature :-)");
        HttpResponse<String> verifyReq = sendRequest(server.getGson().toJson(verifyRequestDto), "GET", "/verify");
        assertEquals(400, verifyReq.statusCode());
    }

    @Test
    void testVerifyEndpoint_incorrectVerifyMsgTooLarge() throws IOException, InterruptedException {
        SignRequestDto signRequestDto = new SignRequestDto("HELLO");
        HttpResponse<String> signReq = sendRequest(server.getGson().toJson(signRequestDto), "GET", "/sign");
        SignResponseDto dto = server.getGson().fromJson(signReq.body(), SignResponseDto.class);

        VerifyRequestDto verifyRequestDto = new VerifyRequestDto("REALLY SUPER LARGE HELLO", dto.getSignature());
        HttpResponse<String> verifyReq = sendRequest(server.getGson().toJson(verifyRequestDto), "GET", "/verify");
        assertEquals(400, verifyReq.statusCode());
    }

}
