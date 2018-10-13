package Algorithms;

import aes.*;
import api.CryptographyAlgorithm;

public class AlgorithmFactory {
    public static CryptographyAlgorithm createAlgorithm(AlgorithmType type) {
        switch (type) {
            case AES:
                return createAesAlgorithm();
            default:
                return createAesAlgorithm();
        }
    }

    private static CryptographyAlgorithm createAesAlgorithm() {
        AESVersion version = AESVersion.AES_128;
        AESEncryptor aesEncryptor = new AESEncryptor();
        AESDecryptor aesDecryptor = new AESDecryptor();
        KeyExpander keyExpander = new KeyExpander(version);

        return new AESAlgorithm(AESVersion.AES_128, aesEncryptor, aesDecryptor, keyExpander);
    }
}
