package ru.yandex.practicum.api.handlers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.api.HttpHmacServer;
import ru.yandex.practicum.services.HmacService;
import ru.yandex.practicum.utils.Config;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class BaseHttpHandlerTest {

    HttpHmacServer server;
    HmacService service;
    Config config;


    @BeforeEach
    void setup(@TempDir Path tempDir) throws IOException {
        setupConfig(tempDir);
        service = new HmacService(config);
        server = new HttpHmacServer(service, config);
        server.start();
    }

    @AfterEach
    void shutdown() {
        server.stop();
    }

    @Test
    void testServerStart_isRunning() {
        assert true;
    }

    protected HttpResponse<String> sendRequest(String content, String method, String appendUrl) throws IOException, InterruptedException {
        // создаём клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create(STR."http://localhost:\{config.getListenPort()}\{appendUrl}");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .method(method, HttpRequest.BodyPublishers.ofString(content))
                    .header("Content-Type", "application/json")
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }

    private void setupConfig(@TempDir Path tempDir) throws IOException {
        String json = """
            {
                "hmacAlg": "HmacSHA256",
                "secret": "AAA",
                "listenPort": 8083,
                "maxMsgSizeBytes": 10
            }
            """;

        File configFile = tempDir.resolve("config.json").toFile();
        Files.writeString(configFile.toPath(), json);
        config = Config.load(configFile.getAbsolutePath());
    }
}
