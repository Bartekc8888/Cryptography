package aes;

public class AESDecryptor {
    private final RijndaelDefinitions rijndaelDefinitions = new RijndaelDefinitions();

    void inverseSubstituteBytes(byte[] dataBlock) {
        for (int index = 0; index < dataBlock.length; index++) {
            dataBlock[index] = rijndaelDefinitions.getInverseSubstitutedByte(dataBlock[index]);
        }
    }

    void inverseShiftRows(byte[] dataBlock) {
        byte[] originalDataBlock = new byte[dataBlock.length];
        System.arraycopy(dataBlock, 0, originalDataBlock, 0, dataBlock.length);

        for (int index = 0; index < dataBlock.length; index++) {
            dataBlock[index] = originalDataBlock[Math.floorMod(index - 4 * (index % 4), 16)];
        }
    }

    void inverseMixColumns(byte[] dataBlock) {
        byte[] mixedDataBlock = new byte[dataBlock.length];

        for (int blockColumnIndex = 0; blockColumnIndex < 4; blockColumnIndex++) {
            for (int rowIndex = 0; rowIndex < 4; rowIndex++) {
                byte[] mixRow = rijndaelDefinitions.getInverseMixRow(rowIndex);

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
