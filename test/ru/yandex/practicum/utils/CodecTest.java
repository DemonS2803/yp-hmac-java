package ru.yandex.practicum.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CodecTest {

    @Test
    void invalidBase64UrlRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> Codec.fromBase64("@@@"));
    }

    @ParameterizedTest
    @MethodSource("base64TestData")
    void validBase64Encoding(String value, String encoded) {
        assertEquals(encoded, Codec.toBase64(value.getBytes()));
    }

    @ParameterizedTest
    @MethodSource("base64TestData")
    void validBase64Decoding(String value, String encoded) {
        assertEquals(value, new String(Codec.fromBase64(encoded)));
    }

    @ParameterizedTest
    @MethodSource("base64TestData")
    void validBase64StringDecoding(String value, String encoded) {
        assertEquals(value, Codec.stringFromBase64(encoded));
    }

    static Stream<Arguments> base64TestData() {
        return Stream.of(
            Arguments.of("Hello", "SGVsbG8"),
            Arguments.of("Hello World!", "SGVsbG8gV29ybGQh"),
            Arguments.of("Test123", "VGVzdDEyMw"),
            Arguments.of("a", "YQ"),
            Arguments.of("ab", "YWI")
        );
    }

    @ParameterizedTest
    @MethodSource("base64Encoded")
    void testCodec_isBase64(String encoded, boolean isValid) {
        assertEquals(isValid, Codec.isBase64(encoded), STR."\{encoded} is \{isValid ? "valid" : "invalid"} base64");
    }

    static Stream<Arguments> base64Encoded() {
        return Stream.of(
                Arguments.of("SGVsbG8sIFdvcmxkIQ==", true),
                Arguments.of("This is not a base64 string!", false),
                Arguments.of("SGVsbG8sIFdvcmxkIQ", true),
                Arguments.of("SGVsbG8", true),
                Arguments.of("VGVzdDEyMw", true),
                Arguments.of("", true)
        );
    }

}
