package elgamal;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import api.CryptographyAlgorithm;
import largeinteger.LargeInteger;

public class ElGamalAlgorithm implements CryptographyAlgorithm {

    private static final int DATA_BLOCK_SIZE = 8;
    private static final int ENCRYPTED_DATA_BLOCK_SIZE = 16;

    public ElGamalAlgorithm() {

    }

    @Override
    public void encrypt(String password, File inputFile, File outputFile) {
        ElGamalPublicKey elGamalPublicKey = KeyConverter.convertFromData(password.getBytes(StandardCharsets.UTF_8));
        encrypt(elGamalPublicKey, inputFile, outputFile);
    }

    @Override
    public byte[] encrypt(String password, String data) {
        ElGamalPublicKey elGamalPublicKey = KeyConverter.convertFromData(password.getBytes(StandardCharsets.UTF_8));

        return encrypt(elGamalPublicKey, data.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public byte[] encrypt(String password, byte[] data) {
        ElGamalPublicKey elGamalPublicKey = KeyConverter.convertFromData(password.getBytes(StandardCharsets.UTF_8));
        return encrypt(elGamalPublicKey, data);
    }

    @Override
    public void decrypt(String password, File inputFile, File outputFile) {
         LargeInteger privateKey = KeyConverter.convertPrivateFromData(password.getBytes(StandardCharsets.UTF_8));

         decrypt(privateKey, inputFile, outputFile);
    }

    @Override
    public byte[] decrypt(String password, byte[] data) {
        LargeInteger privateKey = KeyConverter.convertPrivateFromData(password.getBytes(StandardCharsets.UTF_8));

        return decrypt(privateKey, data);
    }

    public EncryptedBlock encrypt(ElGamalPublicKey publicKey, LargeInteger dataBlock) {
        LargeInteger randomNumber = LargeInteger.createRandom(8);

        LargeInteger c1 = publicKey.getGenerator().modularPower(randomNumber, publicKey.getPrimeNumber());

        LargeInteger dataModulo = dataBlock.modulo(publicKey.getPrimeNumber());
        LargeInteger publicPartToRandomPower = publicKey.getPublicKeyPart().modularPower(randomNumber, publicKey.getPrimeNumber());
        LargeInteger c2 = dataModulo.multiply(publicPartToRandomPower)
                                    .modulo(publicKey.getPrimeNumber());

        return new EncryptedBlock(c1, c2);
    }

    private void encrypt(ElGamalPublicKey key, File inputFile, File outputFile) {
        Metadata emptyMetadata = new Metadata(Metadata.CURRENT_METADATA_VERSION, 0, 0, 0);
        byte[] emptyMetadataBlock = MetadataConverter.createMetadataBlock(emptyMetadata);
        byte[] publicKeyBytes = KeyConverter.convertToData(key);

        long dataLength = 0;
        try (FileInputStream fileInputStream = new FileInputStream(inputFile);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, 65536)) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 65536)) {

                bufferedOutputStream.write(emptyMetadataBlock);
                bufferedOutputStream.write(publicKeyBytes);

                byte[] data = new byte[DATA_BLOCK_SIZE];
                int readBytes;
                while ((readBytes = bufferedInputStream.read(data)) > 0) {
                    dataLength += readBytes;
                    if (readBytes < DATA_BLOCK_SIZE) {
                        byte[] fullBlock = new byte[DATA_BLOCK_SIZE];
                        System.arraycopy(data, 0, fullBlock, 0, readBytes);
                        data = fullBlock;
                    }

                    EncryptedBlock encryptedBlock = encrypt(key, LargeInteger.of(data));

                    byte[] encryptedBytesRandom = new byte[ENCRYPTED_DATA_BLOCK_SIZE];
                    byte[] encryptedBytes = new byte[ENCRYPTED_DATA_BLOCK_SIZE];

                    encryptedBlock.getCipherRandom().toByteArray(encryptedBytesRandom);
                    encryptedBlock.getCipherEncrypted().toByteArray(encryptedBytes);

                    bufferedOutputStream.write(encryptedBytesRandom);
                    bufferedOutputStream.write(encryptedBytes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Encoding failed");
        }

        try (RandomAccessFile randomAccess = new RandomAccessFile(outputFile, "rw")) {
            Metadata metadata = new Metadata(Metadata.CURRENT_METADATA_VERSION, MetadataConverter.numbersLength,
                                             publicKeyBytes.length, dataLength);
            byte[] metadataBlock = MetadataConverter.createMetadataBlock(metadata);

            randomAccess.seek(0);
            randomAccess.write(metadataBlock);
        } catch (IOException e) {
            throw new RuntimeException("Encoding failed", e);
        }
    }

    byte[] encrypt(ElGamalPublicKey key, byte[] data) {
        int metadataLength = MetadataConverter.numbersLength;
        byte[] publicKeyBytes = KeyConverter.convertToData(key);

        Metadata emptyMetadata = new Metadata(Metadata.CURRENT_METADATA_VERSION, metadataLength, publicKeyBytes.length, data.length);
        byte[] emptyMetadataBlock = MetadataConverter.createMetadataBlock(emptyMetadata);

        byte[] encryptedData = new byte[getEncodedDataLength(metadataLength, publicKeyBytes.length, data.length)];

        int offset = publicKeyBytes.length + metadataLength;

        System.arraycopy(emptyMetadataBlock, 0, encryptedData, 0, metadataLength);
        System.arraycopy(publicKeyBytes, 0, encryptedData, metadataLength, publicKeyBytes.length);

        int numberOfBytesToEncode = data.length - DATA_BLOCK_SIZE;
        for (int dataIndex = 0, encryptedIndex = 0;
             dataIndex < numberOfBytesToEncode;
             dataIndex += DATA_BLOCK_SIZE, encryptedIndex += 2*ENCRYPTED_DATA_BLOCK_SIZE) {

            byte[] dataBytes = Arrays.copyOfRange(data, dataIndex, dataIndex + DATA_BLOCK_SIZE);

            EncryptedBlock encryptedBlock = encrypt(key, LargeInteger.of(dataBytes));

            byte[] encryptedBytesRandom = new byte[ENCRYPTED_DATA_BLOCK_SIZE];
            byte[] encryptedBytes = new byte[ENCRYPTED_DATA_BLOCK_SIZE];

            encryptedBlock.getCipherRandom().toByteArray(encryptedBytesRandom);
            encryptedBlock.getCipherEncrypted().toByteArray(encryptedBytes);

            System.arraycopy(encryptedBytesRandom, 0, encryptedData, offset + encryptedIndex, ENCRYPTED_DATA_BLOCK_SIZE);
            System.arraycopy(encryptedBytes, 0, encryptedData,
                             offset + encryptedIndex + encryptedBytesRandom.length,
                             ENCRYPTED_DATA_BLOCK_SIZE);
        }

        // handle last block
        byte[] dataBytes = new byte[DATA_BLOCK_SIZE];
        System.arraycopy(data, numberOfBytesToEncode, dataBytes, 0, (data.length - numberOfBytesToEncode));
        EncryptedBlock encryptedBlock = encrypt(key, LargeInteger.of(dataBytes));

        byte[] encryptedBytesRandom = new byte[ENCRYPTED_DATA_BLOCK_SIZE];
        byte[] encryptedBytes = new byte[ENCRYPTED_DATA_BLOCK_SIZE];

        encryptedBlock.getCipherRandom().toByteArray(encryptedBytesRandom);
        encryptedBlock.getCipherEncrypted().toByteArray(encryptedBytes);

        System.arraycopy(encryptedBytesRandom, 0, encryptedData, encryptedData.length - 2*ENCRYPTED_DATA_BLOCK_SIZE, ENCRYPTED_DATA_BLOCK_SIZE);
        System.arraycopy(encryptedBytes, 0, encryptedData, encryptedData.length - ENCRYPTED_DATA_BLOCK_SIZE, ENCRYPTED_DATA_BLOCK_SIZE);

        return encryptedData;
    }

    public LargeInteger decrypt(ElGamalKeys elGamalKeys, EncryptedBlock encryptedBlock) {
        LargeInteger primeNumber = elGamalKeys.getPublicKey().getPrimeNumber();

        LargeInteger randomModular = encryptedBlock.getCipherRandom().modularPower(elGamalKeys.getPrivateKey(), primeNumber);
        LargeInteger multiplicativeInverse = randomModular.multiplicativeInverse(primeNumber);

        return multiplicativeInverse.multiply(encryptedBlock.getCipherEncrypted()).modulo(primeNumber);
    }

    private void decrypt(LargeInteger key, File inputFile, File outputFile) {
        try (FileInputStream fileInputStream = new FileInputStream(inputFile);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, 65536)) {

            byte[] metadataBytes = new byte[MetadataConverter.numbersLength];
            int metadataBytesRead = bufferedInputStream.read(metadataBytes);
            if (metadataBytesRead != MetadataConverter.numbersLength) {
                throw new RuntimeException("Could not read metadata.");
            }

            Metadata metadata = MetadataConverter.retriveMetadataBlock(metadataBytes);

            byte[] keyBytes = new byte[metadata.getKeyLength()];
            int keyBytesRead = bufferedInputStream.read(keyBytes);

            ElGamalPublicKey elGamalPublicKey = KeyConverter.convertFromData(keyBytes);
            ElGamalKeys allKeys = new ElGamalKeys(key, elGamalPublicKey);

            try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 65536)) {

                long dataLength = 0;
                byte[] data = new byte[2*ENCRYPTED_DATA_BLOCK_SIZE];
                int readBytes;
                while ((readBytes = bufferedInputStream.read(data)) > 0) {
                    dataLength += readBytes;

                    byte[] randomBytes = Arrays.copyOfRange(data, 0, ENCRYPTED_DATA_BLOCK_SIZE);
                    byte[] dataBytes = Arrays.copyOfRange(data, ENCRYPTED_DATA_BLOCK_SIZE, 2*ENCRYPTED_DATA_BLOCK_SIZE);

                    EncryptedBlock encryptedBlock = new EncryptedBlock(LargeInteger.of(randomBytes), LargeInteger.of(dataBytes));
                    LargeInteger decrypted = decrypt(allKeys, encryptedBlock);

                    byte[] decryptedBytes = new byte[DATA_BLOCK_SIZE];
                    decrypted.toByteArray(decryptedBytes);

                    if (metadata.getFileLength() <= dataLength / (2 * ENCRYPTED_DATA_BLOCK_SIZE / DATA_BLOCK_SIZE)) {
                        int lastBlockSize = (int) (metadata.getFileLength() % DATA_BLOCK_SIZE);
                        lastBlockSize = lastBlockSize == 0 ? DATA_BLOCK_SIZE : lastBlockSize;
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

    byte[] decrypt(LargeInteger key, byte[] data) {
        int metadataSize = MetadataConverter.numbersLength;
        byte[] metadataBytes = Arrays.copyOfRange(data, 0, metadataSize);
        Metadata metadata = MetadataConverter.retriveMetadataBlock(metadataBytes);

        int offset = metadata.getKeyLength() + metadataSize;

        byte[] keysBytes = Arrays.copyOfRange(data, metadataSize, metadata.getKeyLength() + metadataSize);
        ElGamalPublicKey elGamalPublicKey = KeyConverter.convertFromData(keysBytes);
        ElGamalKeys allKeys = new ElGamalKeys(key, elGamalPublicKey);

        byte[] decryptedData = new byte[(int) metadata.getFileLength()];

        int realDataLength = (data.length - offset) / (2 * ENCRYPTED_DATA_BLOCK_SIZE / DATA_BLOCK_SIZE);
        int beforeLastBlockOfData = (realDataLength - DATA_BLOCK_SIZE);
        for (int dataIndex = 0, encryptedIndex = offset;
             dataIndex < beforeLastBlockOfData;
             dataIndex += DATA_BLOCK_SIZE, encryptedIndex += 2*ENCRYPTED_DATA_BLOCK_SIZE) {
            byte[] randomBytes = Arrays.copyOfRange(data, encryptedIndex, encryptedIndex + ENCRYPTED_DATA_BLOCK_SIZE);
            byte[] dataBytes = Arrays.copyOfRange(data, encryptedIndex + ENCRYPTED_DATA_BLOCK_SIZE, encryptedIndex + 2*ENCRYPTED_DATA_BLOCK_SIZE);

            EncryptedBlock encryptedBlock = new EncryptedBlock(LargeInteger.of(randomBytes), LargeInteger.of(dataBytes));
            LargeInteger decrypted = decrypt(allKeys, encryptedBlock);

            byte[] decryptedBytes = new byte[DATA_BLOCK_SIZE];
            decrypted.toByteArray(decryptedBytes);

            System.arraycopy(decryptedBytes, 0, decryptedData, dataIndex, DATA_BLOCK_SIZE);
        }

        // handle last block
        int realDataLengthDifference = (int) (realDataLength - metadata.getFileLength());
        int lastBlockSize = (DATA_BLOCK_SIZE - realDataLengthDifference);
        byte[] randomBytes = Arrays.copyOfRange(data, data.length - 2*ENCRYPTED_DATA_BLOCK_SIZE, data.length - ENCRYPTED_DATA_BLOCK_SIZE);
        byte[] dataBytes = Arrays.copyOfRange(data, data.length - ENCRYPTED_DATA_BLOCK_SIZE, data.length);

        EncryptedBlock encryptedBlock = new EncryptedBlock(LargeInteger.of(randomBytes), LargeInteger.of(dataBytes));
        LargeInteger decrypted = decrypt(allKeys, encryptedBlock);

        byte[] decryptedBytes = new byte[DATA_BLOCK_SIZE];
        decrypted.toByteArray(decryptedBytes);

        System.arraycopy(decryptedBytes, DATA_BLOCK_SIZE - lastBlockSize, decryptedData,
                         decryptedData.length - lastBlockSize,
                         lastBlockSize);

        return decryptedData;
    }

    private int getEncodedDataLength(int metadataLength, int keySize, int rawDataLength) {
        int encodedDataLength = rawDataLength;

        if (rawDataLength % DATA_BLOCK_SIZE != 0) {
            encodedDataLength += DATA_BLOCK_SIZE - rawDataLength % DATA_BLOCK_SIZE;
        }

        encodedDataLength *= 2 * ENCRYPTED_DATA_BLOCK_SIZE / DATA_BLOCK_SIZE;
        encodedDataLength += metadataLength + keySize;

        return encodedDataLength;
    }
}
