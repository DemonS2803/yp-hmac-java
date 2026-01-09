package ru.yandex.practicum.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.exceptions.InvalidConfigFormatException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @Test
    void testDefaultValues() {
        Config config = new Config();

        assertEquals("SHA256", config.getHmacAlg());
        assertEquals(8080, config.getListenPort());
        assertEquals(1048576, config.getMaxMsgSizeBytes());
        assertNull(config.getSecret());
    }


    @Test
    void testGetSecretBytes_withEmptySecret() {
        Config config = new Config();
        config.secret = "";

        InvalidConfigFormatException exception = assertThrows(
                InvalidConfigFormatException.class,
                config::getSecretBytes
        );
        assertEquals("Secret is not configured", exception.getMessage());
    }

    @Test
    void testGetSecretBytes_withNullSecret() {
        Config config = new Config();
        config.secret = null;

        InvalidConfigFormatException exception = assertThrows(
                InvalidConfigFormatException.class,
                config::getSecretBytes
        );
        assertEquals("Secret is not configured", exception.getMessage());
    }

    @Test
    void testValidate_success() {
        Config config = new Config();
        config.secret = Base64.getEncoder().encodeToString("test".getBytes());
        config.listenPort = 8080;
        config.maxMsgSizeBytes = 1024;
        config.hmacAlg = "SHA256";

        assertDoesNotThrow(config::validate);
    }

    @Test
    void testValidate_missingSecret() {
        Config config = new Config();
        config.secret = null;

        InvalidConfigFormatException exception = assertThrows(
                InvalidConfigFormatException.class,
                config::validate
        );
        assertEquals("Secret is required", exception.getMessage());
    }

    @Test
    void testValidate_emptySecret() {
        Config config = new Config();
        config.secret = "";

        InvalidConfigFormatException exception = assertThrows(
                InvalidConfigFormatException.class,
                config::validate
        );
        assertEquals("Secret is required", exception.getMessage());
    }

    @Test
    void testValidate_invalidPortLow() {
        Config config = new Config();
        config.secret = "secret";
        config.listenPort = 0;

        InvalidConfigFormatException exception = assertThrows(
                InvalidConfigFormatException.class,
                config::validate
        );
        assertTrue(exception.getMessage().contains("Invalid port number"));
    }

    @Test
    void testValidate_invalidPortHigh() {
        Config config = new Config();
        config.secret = "secret";
        config.listenPort = 70000;

        InvalidConfigFormatException exception = assertThrows(
                InvalidConfigFormatException.class,
                config::validate
        );
        assertTrue(exception.getMessage().contains("Invalid port number"));
    }

    @Test
    void testValidate_invalidMaxMsgSize() {
        Config config = new Config();
        config.secret = "secret";
        config.maxMsgSizeBytes = 0;

        InvalidConfigFormatException exception = assertThrows(
                InvalidConfigFormatException.class,
                config::validate
        );
        assertTrue(exception.getMessage().contains("must be positive"));
    }

    @Test
    void testValidate_negativeMaxMsgSize() {
        Config config = new Config();
        config.secret = "secret";
        config.maxMsgSizeBytes = -1;

        InvalidConfigFormatException exception = assertThrows(
                InvalidConfigFormatException.class,
                config::validate
        );
        assertTrue(exception.getMessage().contains("must be positive"));
    }

    @Test
    void testValidate_invalidHmacAlg() {
        Config config = new Config();
        config.secret = "secret";
        config.hmacAlg = "MD5";

        InvalidConfigFormatException exception = assertThrows(
                InvalidConfigFormatException.class,
                config::validate
        );
        assertTrue(exception.getMessage().contains("Only SHA256 algorithm is supported"));
    }

    @Test
    void testLoad_success(@TempDir Path tempDir) throws IOException {
        Config expectedConfig = new Config();
        expectedConfig.secret = Base64.getEncoder().encodeToString("mySecret".getBytes());
        expectedConfig.listenPort = 9090;
        expectedConfig.maxMsgSizeBytes = 2048;

        File configFile = tempDir.resolve("config.json").toFile();
        try (FileWriter writer = new FileWriter(configFile)) {
            Gson gson = new Gson();
            writer.write(gson.toJson(expectedConfig));
        }

        Config loadedConfig = Config.load(configFile.getAbsolutePath());

        assertEquals(expectedConfig.getSecret(), loadedConfig.getSecret());
        assertEquals(expectedConfig.getListenPort(), loadedConfig.getListenPort());
        assertEquals(expectedConfig.getMaxMsgSizeBytes(), loadedConfig.getMaxMsgSizeBytes());
        assertEquals(expectedConfig.getHmacAlg(), loadedConfig.getHmacAlg());
    }

    @Test
    void testLoad_withHexSecret(@TempDir Path tempDir) throws IOException {
        String json = """
            {
                "secret": "68656c6c6f",
                "listenPort": 8080
            }
            """;

        File configFile = tempDir.resolve("config.json").toFile();
        Files.writeString(configFile.toPath(), json);

        Config loadedConfig = Config.load(configFile.getAbsolutePath());

        assertEquals("68656c6c6f", loadedConfig.getSecret());
        assertEquals(8080, loadedConfig.getListenPort());
        assertDoesNotThrow(loadedConfig::getSecretBytes);
    }

    @Test
    void testLoad_missingFile() {
        assertThrows(IOException.class, () ->
                Config.load("non-existent-file.json")
        );
    }

    @Test
    void testLoad_invalidJson(@TempDir Path tempDir) throws IOException {
        File configFile = tempDir.resolve("config.json").toFile();
        Files.writeString(configFile.toPath(), "{ invalid json");

        assertThrows(JsonSyntaxException.class, () ->
                Config.load(configFile.getAbsolutePath())
        );
    }

    @Test
    void testLoad_invalidConfig(@TempDir Path tempDir) throws IOException {
        String json = """
            {
                "secret": "invalid!!",
                "listenPort": 0
            }
            """;

        File configFile = tempDir.resolve("config.json").toFile();
        Files.writeString(configFile.toPath(), json);

        InvalidConfigFormatException exception = assertThrows(
                InvalidConfigFormatException.class,
                () -> Config.load(configFile.getAbsolutePath())
        );
        assertTrue(exception.getMessage().contains("Invalid port number"));
    }

    @Test
    void testLoad_partialConfig(@TempDir Path tempDir) throws IOException {
        String json = """
            {
                "secret": "c2VjcmV0"
            }
            """;

        File configFile = tempDir.resolve("config.json").toFile();
        Files.writeString(configFile.toPath(), json);

        Config loadedConfig = Config.load(configFile.getAbsolutePath());

        assertEquals("c2VjcmV0", loadedConfig.getSecret());
        // defaults
        assertEquals(8080, loadedConfig.getListenPort());
        assertEquals(1048576, loadedConfig.getMaxMsgSizeBytes());
        assertEquals("SHA256", loadedConfig.getHmacAlg());
    }

}