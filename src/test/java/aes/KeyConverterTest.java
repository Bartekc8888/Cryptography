package aes;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

public class KeyConverterTest {

    @Test
    public void convertPasswordToBytes_isBackwardCompatible() {
        String password = "test string for password to bytes conversion ąę";

        byte[] passwordAsBytes = KeyConverter.convertPasswordToBytes(password);

        Assert.assertEquals(password, new String(passwordAsBytes, StandardCharsets.UTF_8));
    }

    @Test
    public void generateAESKeyFromPassword_isRepeatable() {
        byte[] password = {1, 2, 3, 4, 5, 6, 7, 8};

        byte[] keyFromPassword = KeyConverter.generateAESKeyFromPassword(password, AESVersion.AES_128);
        byte[] keyFromPassword2 = KeyConverter.generateAESKeyFromPassword(password, AESVersion.AES_128);

        Assert.assertArrayEquals(keyFromPassword, keyFromPassword2);
    }

    @Test
    public void generateAESKeyFromPassword_isCorrectLength() {
        byte[] password = {1, 2, 3, 4, 5, 6, 7, 8};

        byte[] keyFromPassword = KeyConverter.generateAESKeyFromPassword(password, AESVersion.AES_128);

        Assert.assertEquals(keyFromPassword.length, AESVersion.AES_128.getKeySizeInBytes());
    }

    @Test
    public void generateAESKeyFromPassword_isBackwardCompatible() {
        byte[] password = {1, 2, 3, 4, 5, 6, 7, 8};
        byte[] expectedKey = {102, -124, 13, -38,
                              21, 78, -118, 17,
                              60, 49, -35, 10,
                              -45, 47, 127, 58};

        byte[] keyFromPassword = KeyConverter.generateAESKeyFromPassword(password, AESVersion.AES_128);

        Assert.assertArrayEquals(expectedKey, keyFromPassword);
    }

    @Test
    public void generateAESKeyFromPassword_doesWorkForLongKeys() {
        byte[] password = {1, 2, 3, 4, 5, 6, 7, 8};

        byte[] keyFromPassword = KeyConverter.generateAESKeyFromPassword(password, AESVersion.AES_256);

        Assert.assertEquals(keyFromPassword.length, AESVersion.AES_256.getKeySizeInBytes());
    }
}