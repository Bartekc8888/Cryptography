package Algorithms;

import aes.*;
import api.CryptographyAlgorithm;

public class AlgorithmFactory {
    public static CryptographyAlgorithm createAlgorithm(AlgorithmType type) {
        switch (type) {
            case AES:
                return createAesAlgorithm(AESVersion.AES_128);
            default:
                return createAesAlgorithm(AESVersion.AES_128);
        }
    }

    public static CryptographyAlgorithm createAESAlgorithm(AESVersion version) {
        return createAesAlgorithm(version);
    }

    private static CryptographyAlgorithm createAesAlgorithm(AESVersion version) {
        AESEncryptor aesEncryptor = new AESEncryptor();
        AESDecryptor aesDecryptor = new AESDecryptor();
        KeyExpander keyExpander = new KeyExpander(version);

        return new AESAlgorithm(version, aesEncryptor, aesDecryptor, keyExpander);
    }
}
