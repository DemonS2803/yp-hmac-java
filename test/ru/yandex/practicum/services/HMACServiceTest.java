package ru.yandex.practicum.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.exceptions.MessageTooLargeException;
import ru.yandex.practicum.utils.Codec;
import ru.yandex.practicum.utils.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class HMACServiceTest {

    HMACService service;
    Config config;

    @BeforeEach
    void setup(@TempDir Path tempDir) throws IOException {
        // using default config values
        setupConfig(tempDir);
        service = new HMACService(config);
    }

    @Test
    void testSign_verifySuccess() throws Exception {
        String sig = service.sign("hello");
        assertTrue(service.verify("hello", sig));
    }

    @Test
    void testOnvalidSignature_mustFails() throws Exception {
        String sig = service.sign("hello");

        // broke sign
        byte[] raw = Codec.fromBase64(sig);
        raw[0] ^= 0x01;
        String badSig = Codec.toBase64(raw);

        assertFalse(service.verify("hello", badSig));
    }

    @Test
    void testModifiedMessage_mustFails() throws Exception {
        String sig = service.sign("hello");
        assertFalse(service.verify("hello!", sig));
    }

    @Test
    void testDeterministicSignature_mustEquals() throws Exception {
        assertEquals(service.sign("hello"), service.sign("hello"));
    }

    @Test
    void testTimingSafeCompareUsed_mustFails() throws Exception {
        String sig = service.sign("hello");
        assertFalse(service.verify("hello", sig.substring(1)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "hellohellomorethan10", "hellohello"})
    void testMaxMsgSizeCheck(String msg) {
        if (msg.length() > config.getMaxMsgSizeBytes()) {
            assertThrows(MessageTooLargeException.class, () -> {
                service.sign(msg);
            });
        } else {
            assertDoesNotThrow(() -> {
                service.sign(msg);
            });
        }
    }

    private void setupConfig(@TempDir Path tempDir) throws IOException {
        String json = """
            {
                "hmacAlg": "HmacSHA256",
                "secret": "68656c6c6f",
                "listenPort": 8080,
                "maxMsgSizeBytes": 10
            }
            """;

        File configFile = tempDir.resolve("config.json").toFile();
        Files.writeString(configFile.toPath(), json);
        config = Config.load("config.json");
    }
}