package elgamal;

import largeinteger.LargeInteger;
import org.junit.Assert;
import org.junit.Test;

public class ElGamalAlgorithmTest {

    @Test
    public void encrypt() {
        LargeInteger mockData = LargeInteger.of("1234567890");
        ElGamalKeys elGamalKeys = ElGamalKeyGenerator.generateKeys();

        ElGamalAlgorithm algorithm = new ElGamalAlgorithm();
        EncryptedBlock encryptedBlock = algorithm.encrypt(elGamalKeys.getPublicKey(), mockData);

        LargeInteger decryptedData = algorithm.decrypt(elGamalKeys, encryptedBlock);

        Assert.assertEquals(mockData, decryptedData);
    }
}