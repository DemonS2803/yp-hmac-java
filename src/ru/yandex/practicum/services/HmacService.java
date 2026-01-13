package ru.yandex.practicum.services;

import ru.yandex.practicum.exceptions.MessageTooLargeException;
import ru.yandex.practicum.utils.Codec;
import ru.yandex.practicum.utils.Config;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HmacService {

    private final byte[] secretKey;
    private final String algorithm;
    private final int maxMsgSizeSizeBytes;

    public HmacService(Config config) {
        this.secretKey = config.getSecretBytes();
        this.algorithm = config.getHmacAlg();
        this.maxMsgSizeSizeBytes = config.getMaxMsgSizeBytes();
    }

    public String sign(String msg) throws NoSuchAlgorithmException, InvalidKeyException {
        validateMsg(msg);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(secretKey, algorithm));
        byte[] sig = mac.doFinal(msg.getBytes());
        return Codec.toBase64(sig);
    }

    public boolean verify(String msg, String signature) throws NoSuchAlgorithmException, InvalidKeyException {
        validateMsg(msg);
        byte[] sig1 = Codec.fromBase64(signature);
        byte[] sig2 = Codec.fromBase64(sign(msg));
        return MessageDigest.isEqual(sig1, sig2);
    }

    private void validateMsg(String msg) {
        validateMsgMaxSize(msg);
    }

    private void validateMsgMaxSize(String msg) {
        if (msg.getBytes().length > maxMsgSizeSizeBytes) {
            throw new MessageTooLargeException(
                    STR."Message is too large: \{msg.getBytes().length}. Only \{maxMsgSizeSizeBytes} available"
            );
        }
    }
}
