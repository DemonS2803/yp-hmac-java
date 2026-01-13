package ru.yandex.practicum;

import ru.yandex.practicum.services.HMACService;
import ru.yandex.practicum.utils.Config;

public class ServerHMAC {
    public static void main(String[] args) throws Exception {
        HMACService service = new HMACService(Config.load(Config.BASE_CONFIG));
        String msg = "some message";
        String sign = service.sign(msg);
        System.out.println(STR."Sign 'msg': \{sign}");
        System.out.println(STR."Verify: \{service.verify(msg, sign)}");
        System.out.println(STR."Invalid Verify: \{service.verify("msg1", sign)}");
    }
}
