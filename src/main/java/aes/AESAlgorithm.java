package aes;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
    public void encrypt(String password, File inputFile, File outputFile) {
        byte[] keyFromPassword = getKeyFromPassword(password);
        encrypt(keyFromPassword, inputFile, outputFile);
    }

    @Override
    public byte[] encrypt(String password, String data) {
        byte[] keyFromPassword = getKeyFromPassword(password);
        return encrypt(keyFromPassword, data);
    }

    @Override
    public byte[] encrypt(String password, byte[] data) {
        byte[] keyFromPassword = getKeyFromPassword(password);
        return encrypt(keyFromPassword, data);
    }

    @Override
    public void decrypt(String password, File inputFile, File outputFile) {
        byte[] keyFromPassword = getKeyFromPassword(password);
        decrypt(keyFromPassword, inputFile, outputFile);
    }

    @Override
    public byte[] decrypt(String password, byte[] data) {
        byte[] keyFromPassword = getKeyFromPassword(password);
        return decrypt(keyFromPassword, data);
    }

    private void encrypt(byte[] key, File inputFile, File outputFile) {
        Metadata emptyMetadata = new Metadata(Metadata.CURRENT_METADATA_VERSION, 0, version);
        byte[] emptyMetadataBlock = MetadataConverter.createMetadataBlock(emptyMetadata);
        List<byte[]> expandedKeys = keyExpander.expandKey(key);

        long dataLength = 0;
        try (FileInputStream fileInputStream = new FileInputStream(inputFile);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, 65536)) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 65536)) {

                bufferedOutputStream.write(emptyMetadataBlock);

                byte[] data = new byte[AES_DATA_BLOCK_SIZE];
                int readBytes;
                while ((readBytes = bufferedInputStream.read(data)) > 0) {
                    dataLength += readBytes;
                    if (readBytes < AES_DATA_BLOCK_SIZE) {
                        byte[] fullBlock = new byte[AES_DATA_BLOCK_SIZE];
                        System.arraycopy(data, 0, fullBlock, 0, readBytes);
                        data = fullBlock;
                    }

                    byte[] encryptedBytes = encodeBlockOfData(expandedKeys, data);
                    bufferedOutputStream.write(encryptedBytes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Encoding failed");
        }

        try (RandomAccessFile randomAccess = new RandomAccessFile(outputFile, "rw")) {
            Metadata metadata = new Metadata(Metadata.CURRENT_METADATA_VERSION, dataLength, version);
            byte[] metadataBlock = MetadataConverter.createMetadataBlock(metadata);

            randomAccess.seek(0);
            randomAccess.write(metadataBlock);
        } catch (IOException e) {
            throw new RuntimeException("Encoding failed", e);
        }
    }


    private byte[] encrypt(byte[] key, String data) {
        return encrypt(key, data.getBytes(StandardCharsets.UTF_8));
    }

    byte[] encrypt(byte[] key, byte[] data) {
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
        System.arraycopy(data, numberOfBytesToEncode, dataBytes, 0, (data.length - numberOfBytesToEncode));
        byte[] encryptedBytes = encodeBlockOfData(expandedKeys, dataBytes);

        System.arraycopy(encryptedBytes, 0, encryptedData, numberOfBytesToEncode + metadataLength, AES_DATA_BLOCK_SIZE);

        return encryptedData;
    }

    private void decrypt(byte[] key, File inputFile, File outputFile) {
        try (FileInputStream fileInputStream = new FileInputStream(inputFile);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, 65536)) {

            byte[] metadataBytes = new byte[Metadata.METADATA_SIZE_IN_BYTES];
            int metadataBytesRead = bufferedInputStream.read(metadataBytes);
            if (metadataBytesRead != Metadata.METADATA_SIZE_IN_BYTES) {
                throw new RuntimeException("Could not read metadata.");
            }

            Metadata metadata = MetadataConverter.retriveMetadataBlock(metadataBytes);

            List<byte[]> expandedKeys = keyExpander.expandKey(key);
            try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 65536)) {

                long dataLength = 0;
                byte[] data = new byte[AES_DATA_BLOCK_SIZE];
                int readBytes;
                while ((readBytes = bufferedInputStream.read(data)) > 0) {
                    dataLength += readBytes;
                    byte[] decryptedBytes = decryptBlockOfData(expandedKeys, data);

                    if (metadata.getFileLength() <= dataLength) {
                        int lastBlockSize = (int) (metadata.getFileLength() % AES_DATA_BLOCK_SIZE);
                        lastBlockSize = lastBlockSize == 0 ? AES_DATA_BLOCK_SIZE : lastBlockSize;
                        byte[] lastBlock = new byte[lastBlockSize];
                        System.arraycopy(decryptedBytes, 0, lastBlock, 0, lastBlockSize);
                        decryptedBytes = lastBlock;
                    }

                    bufferedOutputStream.write(decryptedBytes);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Decoding failed", e);
        }
    }

    byte[] decrypt(byte[] key, byte[] data) {
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

    private byte[] getKeyFromPassword(String password) {
        byte[] passwordBytes = KeyConverter.convertPasswordToBytes(password);
        return KeyConverter.generateAESKeyFromPassword(passwordBytes, version);
    }
}
