package aes;

import java.util.List;

import api.CryptographyAlgorithm;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AESAlgorithm implements CryptographyAlgorithm {

    private final AESVersion version;
    private final AESEncryptor encoder;
    private final KeyExpander keyExpander;

    @Override
    public byte[] encrypt(byte[] key, byte[] data) {
        byte[] encryptedData = new byte[data.length];
        System.arraycopy(data, 0, encryptedData, 0, data.length);

        List<byte[]> expandedKeys = keyExpander.expandKey(key);

        encoder.addRoundKey(key, encryptedData);
        for (int round = 1; round < version.getRoundsCount() - 1; round++) {
            encoder.substituteBytes(encryptedData);
            encoder.shiftRows(encryptedData);
            encoder.mixColumns(encryptedData);
            encoder.addRoundKey(expandedKeys.get(round), encryptedData);
        }

        encoder.substituteBytes(encryptedData);
        encoder.shiftRows(encryptedData);
        encoder.addRoundKey(expandedKeys.get(version.getRoundsCount() - 1), encryptedData);

        return encryptedData;
    }

    @Override
    public byte[] decrypt(byte[] key, byte[] data) {
        return null;
    }

    private byte[] encodeBlockOfData() {
        return null;
    }
}
