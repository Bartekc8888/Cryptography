package aes;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class KeyExpanderTest {

    private KeyExpander keyExpander;

    @Before
    public void setUp() {
        keyExpander = new KeyExpander(AESVersion.AES_128);
    }

    @Test
    public void expandKey() {
        byte[] baseKey = {0x54, 0x68, 0x61, 0x74,
                          0x73, 0x20, 0x6D, 0x79,
                          0x20, 0x4B, 0x75, 0x6E,
                          0x67, 0x20, 0x46, 0x75};

        byte[] firstRoundKey = {(byte) 0xE2, 0x32, (byte) 0xFC, (byte) 0xF1,
                                (byte) 0x91, 0x12, (byte) 0x91, (byte) 0x88,
                                (byte) 0xB1, 0x59, (byte) 0xE4, (byte) 0xE6,
                                (byte) 0xD6, 0x79, (byte) 0xA2, (byte) 0x93};

        List<byte[]> expandedKeys = keyExpander.expandKey(baseKey);

        Assert.assertArrayEquals(firstRoundKey, expandedKeys.get(1));
    }
}