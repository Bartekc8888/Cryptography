package aes;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AESDecryptorTest {

    private AESDecryptor aesDecryptor;

    @Before
    public void setUp() {
        aesDecryptor = new AESDecryptor();
    }

    @Test
    public void inverseSubstituteBytes() {
        byte[] testBlockOfData = {0x63, (byte) 0xC0, (byte) 0xAB, 0x20,
                                  (byte) 0xEB, 0x2F, 0x30, (byte) 0xCB,
                                  (byte) 0x9F, (byte) 0x93, (byte) 0xAF, 0x2B,
                                  (byte) 0xA0, (byte) 0x92, (byte) 0xC7, (byte) 0xA2};

        byte[] expectedBlockOfData = {0x00, 0x1F, 0x0E, 0x54,
                                      0x3C, 0x4E, 0x08, 0x59,
                                      0x6E, 0x22, 0x1B, 0x0B,
                                      0x47, 0x74, 0x31, 0x1A};

        aesDecryptor.inverseSubstituteBytes(testBlockOfData);

        Assert.assertArrayEquals(expectedBlockOfData, testBlockOfData);
    }

    @Test
    public void inverseShiftRows() {
        byte[] testBlockOfData = {0x63, 0x2F, (byte) 0xAF, (byte) 0xA2,
                                  (byte) 0xEB, (byte) 0x93, (byte) 0xC7, 0x20,
                                  (byte) 0x9F, (byte) 0x92, (byte) 0xAB, (byte) 0xCB,
                                  (byte) 0xA0, (byte) 0xC0, 0x30, 0x2B};

        byte[] expectedBlockOfData = {0x63, (byte) 0xC0, (byte) 0xAB, 0x20,
                                     (byte) 0xEB, 0x2F, 0x30, (byte) 0xCB,
                                     (byte) 0x9F, (byte) 0x93, (byte) 0xAF, 0x2B,
                                     (byte) 0xA0, (byte) 0x92, (byte) 0xC7, (byte) 0xA2};

        aesDecryptor.inverseShiftRows(testBlockOfData);

        Assert.assertArrayEquals(expectedBlockOfData, testBlockOfData);
    }

    @Test
    public void inverseMixColumns() {
        byte[] testBlockOfData = {(byte) 0xBA, 0x75, (byte) 0xF4, 0x7A,
                                   (byte) 0x84, (byte) 0xA4, (byte) 0x8D, 0x32,
                                   (byte) 0xE8, (byte) 0x8D, 0x06, 0x0E,
                                   0x1B, 0x40, 0x7D, 0x5D};

        byte[] expectedBlockOfData = {0x63, 0x2F, (byte) 0xAF, (byte) 0xA2,
                                      (byte) 0xEB, (byte) 0x93, (byte) 0xC7, 0x20,
                                      (byte) 0x9F, (byte) 0x92, (byte) 0xAB, (byte) 0xCB,
                                      (byte) 0xA0, (byte) 0xC0, 0x30, 0x2B};

        aesDecryptor.inverseMixColumns(testBlockOfData);

        Assert.assertArrayEquals(expectedBlockOfData, testBlockOfData);
    }
}