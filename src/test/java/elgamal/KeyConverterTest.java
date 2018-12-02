package elgamal;

import org.junit.Assert;
import org.junit.Test;

public class KeyConverterTest {

    @Test
    public void convertPublicKeysToData() {
        ElGamalKeys elGamalKeys = ElGamalKeyGenerator.generateKeys();

        byte[] bytesOfKeys = KeyConverter.convertToData(elGamalKeys.getPublicKey());
        ElGamalPublicKey elGamalPublicKey = KeyConverter.convertFromData(bytesOfKeys);

        Assert.assertEquals(elGamalKeys.getPublicKey(), elGamalPublicKey);
    }
}