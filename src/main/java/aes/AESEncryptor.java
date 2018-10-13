package aes;

public class AESEncryptor {
    private final RijndaelDefinitions rijndaelDefinitions = new RijndaelDefinitions();

    void addRoundKey(byte[] roundKey, byte[] dataBlock) {
        for (int index = 0; index < dataBlock.length; index++) {
            dataBlock[index] ^= roundKey[index];
        }
    }

    void substituteBytes(byte[] dataBlock) {
        for (int index = 0; index < dataBlock.length; index++) {
            dataBlock[index] = rijndaelDefinitions.getSubstitutedByte(dataBlock[index]);
        }
    }

    void shiftRows(byte[] dataBlock) {
        byte[] originalDataBlock = new byte[dataBlock.length];
        System.arraycopy(dataBlock, 0, originalDataBlock, 0, dataBlock.length);

        for (int index = 0; index < 4; index++) {
            dataBlock[index * 4 + 1] = originalDataBlock[((index + 1) * 5 - index) % 16];
            dataBlock[index * 4 + 2] = originalDataBlock[((index + 2) * 5 - index) % 16];
            dataBlock[index * 4 + 3] = originalDataBlock[((index + 3) * 5 - index) % 16];
        }
    }

    void mixColumns(byte[] dataBlock) {
        byte[] mixedDataBlock = new byte[dataBlock.length];

        for (int blockColumnIndex = 0; blockColumnIndex < 4; blockColumnIndex++) {
            for (int rowIndex = 0; rowIndex < 4; rowIndex++) {
                byte[] mixRow = rijndaelDefinitions.getMixRow(rowIndex);

                byte dotProduct = 0;
                for (int columnIndex = 0; columnIndex < mixRow.length; columnIndex++) {
                    byte datablockValueFromColumn = dataBlock[blockColumnIndex * 4 + columnIndex];
                    byte multiplier = mixRow[columnIndex];
                    dotProduct ^= rijndaelDefinitions.lookupMultiplication(datablockValueFromColumn, multiplier);
                }

                mixedDataBlock[blockColumnIndex * 4 + rowIndex] = dotProduct;
            }
        }

        System.arraycopy(mixedDataBlock, 0, dataBlock, 0, dataBlock.length);
    }

}
