package ru.yandex.practicum.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.api.handlers.SignHmacHttpHandler;
import ru.yandex.practicum.api.handlers.VerifyHmacHttpHandler;
import ru.yandex.practicum.api.utils.HttpConstants;
import ru.yandex.practicum.services.HmacService;
import ru.yandex.practicum.utils.Config;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class HttpHmacServer {
    private static final Logger log = Logger.getLogger(HttpHmacServer.class.getName());

    protected int port;
    protected HmacService service;
    protected HttpServer server;
    protected Config config;
    protected Gson gson;

    public HttpHmacServer(HmacService service, Config config) {
        this.service = service;
        this.config = config;
        this.port = config.getListenPort();
        getGson();
    }

    public void start() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);

        log.info("Create context for HTTP server");
        server.createContext(HttpConstants.SIGN_URL, new SignHmacHttpHandler(service, config, getGson()));
        server.createContext(HttpConstants.VERIFY_URL, new VerifyHmacHttpHandler(service, config, getGson()));

        log.info("Start HTTP server");
        this.server.start();
    }

    public void stop() {
        server.stop(1);
    }

    public Gson getGson() {
        if (gson == null) {
            this.gson = new GsonBuilder()
                    .serializeNulls()
                    .setPrettyPrinting()
                    .create();
        }
        return gson;
    }

}
