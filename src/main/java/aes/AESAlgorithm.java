package aes;

import java.util.List;

import api.CryptographyAlgorithm;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AESAlgorithm implements CryptographyAlgorithm {

    private final AESVersion version;
    private final AESEncryptor encoder;
    private final AESDecryptor decryptor;
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
        byte[] decryptedData = new byte[data.length];
        System.arraycopy(data, 0, decryptedData, 0, data.length);

        List<byte[]> expandedKeys = keyExpander.expandKey(key);

        encoder.addRoundKey(expandedKeys.get(version.getRoundsCount() - 1), decryptedData);
        decryptor.inverseShiftRows(decryptedData);
        decryptor.inverseSubstituteBytes(decryptedData);

        for (int round = version.getRoundsCount() - 2; round > 0; round--) {
            encoder.addRoundKey(expandedKeys.get(round), decryptedData);
            decryptor.inverseMixColumns(decryptedData);
            decryptor.inverseShiftRows(decryptedData);
            decryptor.inverseSubstituteBytes(decryptedData);
        }

        encoder.addRoundKey(key, decryptedData);

        return decryptedData;
    }

    private byte[] encodeBlockOfData() {
        return null;
    }
}
