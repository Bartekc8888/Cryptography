package aes;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AESAlgorithmTest {

    private AESAlgorithm aesAlgorithm;

    @Before
    public void setUp() {
        AESVersion version = AESVersion.AES_128;
        AESEncryptor aesEncryptor = new AESEncryptor();
        AESDecryptor aesDecryptor = new AESDecryptor();
        KeyExpander keyExpander = new KeyExpander(version);
        aesAlgorithm = new AESAlgorithm(version, aesEncryptor, aesDecryptor, keyExpander);
    }

    @Test
    public void encrypt() {
        byte[] testKey = {0x54, 0x68, 0x61, 0x74,
                          0x73, 0x20, 0x6D, 0x79,
                          0x20, 0x4B, 0x75, 0x6E,
                          0x67, 0x20, 0x46, 0x75};

        byte[] testBlockOfData = {0x54, 0x77, 0x6F, 0x20,
                                  0x4F, 0x6E, 0x65, 0x20,
                                  0x4E, 0x69, 0x6E, 0x65,
                                  0x20, 0x54, 0x77, 0x6F};

        byte[] expectedOutput = {0x29, (byte) 0xC3, 0x50, 0x5F,
                                 0x57, 0x14, 0x20, (byte) 0xF6,
                                 0x40, 0x22, (byte) 0x99, (byte) 0xB3,
                                 0x1A, 0x02, (byte) 0xD7, 0x3A};

        byte[] encryptedData = aesAlgorithm.encrypt(testKey, testBlockOfData);
        byte[] dataBytes = Arrays.copyOfRange(encryptedData, Metadata.METADATA_SIZE_IN_BYTES, encryptedData.length);

        Assert.assertArrayEquals(expectedOutput, dataBytes);
    }

    @Test
    public void encryptPartial() {
        byte[] testKey = {0x54, 0x68, 0x61, 0x74,
                          0x73, 0x20, 0x6D, 0x79,
                          0x20, 0x4B, 0x75, 0x6E,
                          0x67, 0x20, 0x46, 0x75};

        byte[] testBlockOfData = {0x54, 0x77, 0x6F, 0x20,
                                  0x4F, 0x6E, 0x65, 0x20,
                                  0x4E, 0x69, 0x6E, 0x65,
                                  0x20, 0x54, 0x77};

        byte[] expectedOutput = {-32, -27, 119, 70,
                                 -12, 15, -121, -100,
                                 126, 82, 110, -48,
                                 -74, -87, 75, -93};

        byte[] encryptedData = aesAlgorithm.encrypt(testKey, testBlockOfData);
        byte[] dataBytes = Arrays.copyOfRange(encryptedData, Metadata.METADATA_SIZE_IN_BYTES, encryptedData.length);

        Assert.assertArrayEquals(expectedOutput, dataBytes);
    }

    @Test
    public void decrypt() {
        byte[] testKey = {0x54, 0x68, 0x61, 0x74,
                          0x73, 0x20, 0x6D, 0x79,
                          0x20, 0x4B, 0x75, 0x6E,
                          0x67, 0x20, 0x46, 0x75};

        byte[] testBlockOfData = {0, 0, 0, 1,
                                  0, 0, 0, 0,
                                  0, 0, 0, 16,
                                  65, 49, 50, 56, // metadata
                                  0x29, (byte) 0xC3, 0x50, 0x5F,
                                  0x57, 0x14, 0x20, (byte) 0xF6,
                                  0x40, 0x22, (byte) 0x99, (byte) 0xB3,
                                  0x1A, 0x02, (byte) 0xD7, 0x3A};

        byte[] expectedOutput = {0x54, 0x77, 0x6F, 0x20,
                                  0x4F, 0x6E, 0x65, 0x20,
                                  0x4E, 0x69, 0x6E, 0x65,
                                  0x20, 0x54, 0x77, 0x6F};

        byte[] decryptedData = aesAlgorithm.decrypt(testKey, testBlockOfData);

        Assert.assertArrayEquals(expectedOutput, decryptedData);
    }

    @Test
    public void decryptPartial() {
        byte[] testKey = {0x54, 0x68, 0x61, 0x74,
                          0x73, 0x20, 0x6D, 0x79,
                          0x20, 0x4B, 0x75, 0x6E,
                          0x67, 0x20, 0x46, 0x75};

        byte[] testBlockOfData = {0, 0, 0, 1,
                                  0, 0, 0, 0,
                                  0, 0, 0, 15,
                                  65, 49, 50, 56, // metadata
                                  -32, -27, 119, 70,
                                  -12, 15, -121, -100,
                                  126, 82, 110, -48,
                                  -74, -87, 75, -93};

        byte[] expectedOutput = {0x54, 0x77, 0x6F, 0x20,
                                 0x4F, 0x6E, 0x65, 0x20,
                                 0x4E, 0x69, 0x6E, 0x65,
                                 0x20, 0x54, 0x77};

        byte[] decryptedData = aesAlgorithm.decrypt(testKey, testBlockOfData);

        Assert.assertArrayEquals(expectedOutput, decryptedData);
    }
}