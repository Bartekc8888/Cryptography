package aes;

import java.util.Arrays;
import java.util.List;

import api.CryptographyAlgorithm;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AESAlgorithm implements CryptographyAlgorithm {

    private static final int AES_DATA_BLOCK_SIZE = 16;

    private final AESVersion version;
    private final AESEncryptor encoder;
    private final AESDecryptor decryptor;
    private final KeyExpander keyExpander;

    @Override
    public byte[] encrypt(byte[] key, byte[] data) {
        Metadata metadata = new Metadata(Metadata.CURRENT_METADATA_VERSION, data.length, version);
        byte[] metadataBlock = MetadataConverter.createMetadataBlock(metadata);
        int metadataLength = metadataBlock.length;

        byte[] encryptedData = new byte[getEncodedDataLength(metadataLength, data.length)];
        System.arraycopy(metadataBlock, 0, encryptedData, 0, metadataLength);

        int numberOfBytesToEncode = (encryptedData.length - metadataLength) - AES_DATA_BLOCK_SIZE;
        List<byte[]> expandedKeys = keyExpander.expandKey(key);
        for (int dataIndex = 0; dataIndex < numberOfBytesToEncode; dataIndex += AES_DATA_BLOCK_SIZE) {
            byte[] dataBytes = Arrays.copyOfRange(data, dataIndex, dataIndex + AES_DATA_BLOCK_SIZE);
            byte[] encryptedBytes = encodeBlockOfData(expandedKeys, dataBytes);

            System.arraycopy(encryptedBytes, 0, encryptedData, metadataLength + dataIndex, AES_DATA_BLOCK_SIZE);
        }

        // handle last block
        byte[] dataBytes = new byte[AES_DATA_BLOCK_SIZE];
        System.arraycopy(data, numberOfBytesToEncode, dataBytes, 0, data.length);
        byte[] encryptedBytes = encodeBlockOfData(expandedKeys, dataBytes);

        System.arraycopy(encryptedBytes, 0, encryptedData, numberOfBytesToEncode + metadataLength, AES_DATA_BLOCK_SIZE);

        return encryptedData;
    }

    @Override
    public byte[] decrypt(byte[] key, byte[] data) {
        int metadataSize = Metadata.METADATA_SIZE_IN_BYTES;
        byte[] metadataBytes = Arrays.copyOfRange(data, 0, metadataSize);
        Metadata metadata = MetadataConverter.retriveMetadataBlock(metadataBytes);

        byte[] decryptedData = new byte[(int) metadata.getFileLength()];

        int beforeLastBlockOfData = (data.length - AES_DATA_BLOCK_SIZE);
        List<byte[]> expandedKeys = keyExpander.expandKey(key);
        for (int dataIndex = metadataSize; dataIndex < beforeLastBlockOfData; dataIndex += AES_DATA_BLOCK_SIZE) {
            byte[] dataBytes = Arrays.copyOfRange(data, dataIndex, dataIndex + AES_DATA_BLOCK_SIZE);
            byte[] decryptedBytes = decryptBlockOfData(expandedKeys, dataBytes);

            int decryptedIndex = dataIndex - metadataSize;
            System.arraycopy(decryptedBytes, 0, decryptedData, decryptedIndex, AES_DATA_BLOCK_SIZE);
        }

        // handle last block
        int realDataLengthDifference = (int) ((data.length - metadataSize) - metadata.getFileLength());
        int lastBlockSize = (AES_DATA_BLOCK_SIZE - realDataLengthDifference);
        byte[] dataBytes = Arrays.copyOfRange(data, beforeLastBlockOfData, data.length);
        byte[] decryptedBytes = decryptBlockOfData(expandedKeys, dataBytes);
        System.arraycopy(decryptedBytes, 0, decryptedData,
                         decryptedData.length - lastBlockSize,
                         lastBlockSize);

        return decryptedData;
    }

    private byte[] encodeBlockOfData(List<byte[]> expandedKeys, byte[] dataToEncrypt) {
        encoder.addRoundKey(expandedKeys.get(0), dataToEncrypt);
        for (int round = 1; round < version.getRoundsCount() - 1; round++) {
            encoder.substituteBytes(dataToEncrypt);
            encoder.shiftRows(dataToEncrypt);
            encoder.mixColumns(dataToEncrypt);
            encoder.addRoundKey(expandedKeys.get(round), dataToEncrypt);
        }

        encoder.substituteBytes(dataToEncrypt);
        encoder.shiftRows(dataToEncrypt);
        encoder.addRoundKey(expandedKeys.get(version.getRoundsCount() - 1), dataToEncrypt);

        return dataToEncrypt;
    }

    private byte[] decryptBlockOfData(List<byte[]> expandedKeys, byte[] dataToDecrypt) {
        encoder.addRoundKey(expandedKeys.get(version.getRoundsCount() - 1), dataToDecrypt);
        decryptor.inverseShiftRows(dataToDecrypt);
        decryptor.inverseSubstituteBytes(dataToDecrypt);

        for (int round = version.getRoundsCount() - 2; round > 0; round--) {
            encoder.addRoundKey(expandedKeys.get(round), dataToDecrypt);
            decryptor.inverseMixColumns(dataToDecrypt);
            decryptor.inverseShiftRows(dataToDecrypt);
            decryptor.inverseSubstituteBytes(dataToDecrypt);
        }

        encoder.addRoundKey(expandedKeys.get(0), dataToDecrypt);

        return dataToDecrypt;
    }

    private int getEncodedDataLength(int metadataLength, int rawDataLength) {
        int encodedDataLength = metadataLength + rawDataLength;

        if (rawDataLength % AES_DATA_BLOCK_SIZE != 0) {
            encodedDataLength += AES_DATA_BLOCK_SIZE - rawDataLength % AES_DATA_BLOCK_SIZE;
        }

        return encodedDataLength;
    }
}
