package ru.yandex.practicum.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.yandex.practicum.exceptions.InvalidConfigFormatException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CliRotateSecret {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        if (args.length != 2) {
            printHelp();
            System.exit(1);
        }

        String configFile = args[0];
        String newSecret = args[1];
        rotateSecret(configFile, newSecret);
    }

    public static void rotateSecret(String configFile, String newSecret) {
        try {
            checkConfigExists(configFile);
            String newEncodedSecret = Codec.toBase64(newSecret.getBytes());
            validateConfigWithNewSecret(configFile, newEncodedSecret);

            JsonObject jsonObject = loadConfigAsJson(configFile);
            jsonObject.addProperty("secret", newEncodedSecret);

            writeConfig(jsonObject, configFile);

            // validate na vsyakiy sluchai
            Config updatedConfig = Config.load(configFile);
            updatedConfig.validate();

            System.out.println(STR."Successfully rotated secret in: \{configFile}");
            System.out.println(STR."New secret generated: \{newEncodedSecret}");

            System.out.println("Secret Information:");
            System.out.println("Format: Base64");
            System.out.println(STR."Length: \{newSecret.length()} characters");
            System.out.println(STR."Key size: \{updatedConfig.getSecretBytes().length * 8} bits");

        } catch (InvalidConfigFormatException e) {
            System.err.println(STR."Config validation failed after rotation: \{e.getMessage()}");
            System.exit(1);
        } catch (IOException e) {
            System.err.println(STR."Error read/write config file: \{e.getMessage()}");
            System.exit(1);
        } catch (Exception e) {
            System.err.println(STR."Unexpected error: \{e.getMessage()}");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void checkConfigExists(String configFilePath) {
        File configFile = new File(configFilePath);
        if (!configFile.exists()) {
            System.err.println(STR."Config file not found: \{configFilePath}");
            System.exit(1);
        }
    }

    private static JsonObject loadConfigAsJson(String configFilePath) throws IOException {
        JsonObject jsonObject;
        try (FileReader reader = new FileReader(configFilePath)) {
            jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
        }
        return jsonObject;
    }

    private static void writeConfig(JsonObject jsonObject, String configFile) throws IOException {
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(jsonObject, writer);
            writer.flush();
        }
    }

    private static void validateConfigWithNewSecret(String configFile, String newSecret) throws IOException {
        Config config = Config.load(configFile);
        config.secret = newSecret;
        config.validate();
    }

    private static void printHelp() {
        System.err.println("Usage: rotate-secret <config-file> <new-secret");
        System.err.println("Example: rotate-secret config.json true-secret");
    }

}