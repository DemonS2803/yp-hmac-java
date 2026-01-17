package ru.yandex.practicum.services;

import ru.yandex.practicum.exceptions.InvalidSignatureFormatException;
import ru.yandex.practicum.exceptions.MessageIsEmptyException;
import ru.yandex.practicum.exceptions.MessageTooLargeException;
import ru.yandex.practicum.utils.Codec;
import ru.yandex.practicum.utils.Config;
import ru.yandex.practicum.utils.ValidationUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HmacService {

    final byte[] secretKey;
    final String algorithm;
    final Config config;

    public HmacService(Config config) {
        this.secretKey = config.getSecretBytes();
        this.algorithm = config.getHmacAlg();
        this.config = config;
    }

    public String sign(String msg) throws NoSuchAlgorithmException, InvalidKeyException {
        ValidationUtils.validateMsg(msg, config);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(secretKey, algorithm));
        byte[] sig = mac.doFinal(msg.getBytes());
        return Codec.toBase64(sig);
    }

    public boolean verify(String msg, String signature)
            throws NoSuchAlgorithmException, InvalidKeyException, InvalidSignatureFormatException {
        ValidationUtils.validateMsg(msg, config);
        ValidationUtils.validateSignatureIsBase64(signature);
        byte[] sig1 = Codec.fromBase64(signature);
        byte[] sig2 = Codec.fromBase64(sign(msg));
        return MessageDigest.isEqual(sig1, sig2);
    }

}
