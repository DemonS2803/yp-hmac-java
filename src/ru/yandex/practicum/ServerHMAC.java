package ru.yandex.practicum;

import ru.yandex.practicum.api.HttpHmacServer;
import ru.yandex.practicum.services.HmacService;
import ru.yandex.practicum.utils.Config;

public class ServerHMAC {
    public static void main(String[] args) throws Exception {
        Config config = Config.load(Config.BASE_CONFIG);
        HmacService service = new HmacService(config);

        // разделение на случай добавления иных методов взаимодействия (rpc, cli)
        HttpHmacServer server = new HttpHmacServer(service, config);

        server.start();
//        server.stop();
    }
}
