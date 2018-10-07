package aes;

import api.CryptographyAlgorithm;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AESAlgorithm implements CryptographyAlgorithm {

    private final AESVersion version;
    private final RijndaelDefinitions rijndaelDefinitions = new RijndaelDefinitions();

    @Override
    public byte[] encode(byte[] key, byte[] data) {
        addRoundKey(key, data);

        for (int round = 0; round < version.getRoundsCount(); round++) {
            substituteBytes(data);
            shiftRows(data);
            mixColumns(data);
            addRoundKey(key, data);
        }

        substituteBytes(data);
        shiftRows(data);
        addRoundKey(key, data);

        return null;
    }

    @Override
    public byte[] decode(byte[] key, byte[] data) {
        return null;
    }

    private byte[] encodeBlockOfData() {
        return null;
    }

    private void addRoundKey(byte[] roundKey, byte[] dataBlock) {
        for (int index = 0; index < dataBlock.length; index++) {
            dataBlock[index] ^= roundKey[index];
        }
    }

    private void substituteBytes(byte[] dataBlock) {
        for (int index = 0; index < dataBlock.length; index++) {
            dataBlock[index] = rijndaelDefinitions.getSubstitutedByte(dataBlock[index]);
        }
    }

    private void shiftRows(byte[] dataBlock) {
        for (int index = 0; index < dataBlock.length; index += 4) {
            dataBlock[index + 1] = dataBlock[(index + 5) % 16];
            dataBlock[index + 2] = dataBlock[(index + 10) % 16];
            dataBlock[index + 3] = dataBlock[(index + 15) % 16];
        }
    }

    private void mixColumns(byte[] dataBlock) {

    }
}
