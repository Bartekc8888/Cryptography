package Algorithms;

import aes.AESAlgorithm;
import aes.AESEncryptor;
import aes.AESVersion;
import aes.KeyExpander;
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
        KeyExpander keyExpander = new KeyExpander(version);

        return new AESAlgorithm(AESVersion.AES_128, aesEncryptor, keyExpander);
    }
}
