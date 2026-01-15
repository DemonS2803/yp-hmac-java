package ru.yandex.practicum.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.exceptions.InvalidConfigFormatException;
import ru.yandex.practicum.services.HmacService;
import ru.yandex.practicum.utils.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpHmacServerTest {

    HttpHmacServer server;
    HmacService service;
    Config config;

    @Test
    void testHttpHmacServer_startWithValidConfig(@TempDir Path tempDir) throws IOException {
        assertDoesNotThrow(() -> {
            loadValidConfig(tempDir);
            service = new HmacService(config);
            server = new HttpHmacServer(service, config);

            server.start();
            server.stop();
        });
    }

    private void loadValidConfig(@TempDir Path tempDir) throws IOException {
        String json = """
            {
                "hmacAlg": "HmacSHA256",
                "secret": "AAA",
                "listenPort": 8083
            }
            """;

        File configFile = tempDir.resolve("config.json").toFile();
        Files.writeString(configFile.toPath(), json);
        config = Config.load(configFile.getAbsolutePath());
    }

    @Test
    void testHttpHmacServer_dontStartWithInvalidConfig(@TempDir Path tempDir) throws IOException {
        assertThrows(InvalidConfigFormatException.class, () -> {
            loadInvalidConfig(tempDir);
            service = new HmacService(config);
            server = new HttpHmacServer(service, config);

            server.start();
            server.stop();
        });
    }

    private void loadInvalidConfig(@TempDir Path tempDir) throws IOException {
        String json = """
            {
                "hmacAlg": "HmacSHA512",
                "secret": "AAA",
                "listenPort": 8083
            }
            """;

        File configFile = tempDir.resolve("config.json").toFile();
        Files.writeString(configFile.toPath(), json);
        config = Config.load(configFile.getAbsolutePath());
    }

}