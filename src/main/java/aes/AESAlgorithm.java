package aes;

import api.CryptographyAlgorithm;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AESAlgorithm implements CryptographyAlgorithm {

    private final AESVersion version;
    private final AESEncryptor encoder = new AESEncryptor();

    @Override
    public byte[] encrypt(byte[] key, byte[] data) {
        encoder.addRoundKey(key, data);

        for (int round = 0; round < version.getRoundsCount(); round++) {
            encoder.substituteBytes(data);
            encoder.shiftRows(data);
            encoder.mixColumns(data);
            encoder.addRoundKey(key, data);
        }

        encoder.substituteBytes(data);
        encoder.shiftRows(data);
        encoder.addRoundKey(key, data);

        return null;
    }

    @Override
    public byte[] decrypt(byte[] key, byte[] data) {
        return null;
    }

    private byte[] encodeBlockOfData() {
        return null;
    }
}
