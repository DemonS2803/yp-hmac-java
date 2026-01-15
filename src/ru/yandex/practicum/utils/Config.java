package ru.yandex.practicum.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import ru.yandex.practicum.exceptions.InvalidConfigFormatException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Logger;

public class Config {
    private static final Logger log = Logger.getLogger(Config.class.getName());

    public static final String BASE_CONFIG = "config.json";

    String hmacAlg = "HmacSHA256";
    String secret;
    int listenPort = 8080;
    int maxMsgSizeBytes = 1048576;
    int maxRequestBodySizeBytes = 1048576;

    public String getHmacAlg() {
        return hmacAlg;
    }

    public String getSecret() {
        return secret;
    }

    public int getListenPort() {
        return listenPort;
    }

    public int getMaxMsgSizeBytes() {
        return maxMsgSizeBytes;
    }

    public int getMaxRequestBodySizeBytes() {
        return maxRequestBodySizeBytes;
    }

    public byte[] getSecretBytes() {
        if (secret == null || secret.isEmpty()) {
            throw new InvalidConfigFormatException("Secret is not configured");
        }

        try {
            return Base64.getDecoder().decode(secret);
        } catch (InvalidConfigFormatException e1) {
            try {
                return hexStringToByteArray(secret);
            } catch (InvalidConfigFormatException e2) {
                log.severe("Invalid secret format");
                throw new InvalidConfigFormatException(
                        STR."Secret must be valid Base64 or Hex string: \{e1.getMessage()}");
            }
        }
    }

    private static byte[] hexStringToByteArray(String hex) {
        if (hex.length() % 2 != 0) {
            throw new InvalidConfigFormatException("Hex string must have even length");
        }

        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int index = i * 2;
            int val = Integer.parseInt(hex.substring(index, index + 2), 16);
            bytes[i] = (byte) val;
        }
        return bytes;
    }

    public void validate() throws InvalidConfigFormatException {
        if (secret == null || secret.isEmpty()) {
            throw new InvalidConfigFormatException("Secret is required");
        }

        if (listenPort < 1 || listenPort > 65535) {
            throw new InvalidConfigFormatException(STR."Invalid port number: \{listenPort}");
        }

        if (maxMsgSizeBytes <= 0) {
            throw new InvalidConfigFormatException("maxMsgSizeBytes must be positive");
        }

        if (maxRequestBodySizeBytes <= 0) {
            throw new InvalidConfigFormatException("maxRequestBodySizeBytes must be positive");
        }

        if (!hmacAlg.equals("HmacSHA256")) {
            throw new InvalidConfigFormatException("Only HmacSHA256 algorithm is supported");
        }

        getSecretBytes();
    }

    public static Config load(String filename) throws IOException, JsonSyntaxException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filename)) {
            Config config = gson.fromJson(reader, Config.class);
            config.validate();
            return config;
        } catch (Exception e) {
            log.severe(STR."Failed to read config <\{filename}>");
            throw e;
        }
    }
}