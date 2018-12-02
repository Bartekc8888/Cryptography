package elgamal;

import java.nio.charset.StandardCharsets;

import largeinteger.LargeInteger;
import org.junit.Assert;
import org.junit.Test;

public class ElGamalAlgorithmTest {

    @Test
    public void encryptBlockRandom() {
        LargeInteger mockData = LargeInteger.of("1234567890");
        ElGamalKeys elGamalKeys = ElGamalKeyGenerator.generateKeys();

        ElGamalAlgorithm algorithm = new ElGamalAlgorithm();
        EncryptedBlock encryptedBlock = algorithm.encrypt(elGamalKeys.getPublicKey(), mockData);

        LargeInteger decryptedData = algorithm.decrypt(elGamalKeys, encryptedBlock);

        Assert.assertEquals(mockData, decryptedData);
    }

    @Test
    public void encryptBlock() {
        LargeInteger mockData = LargeInteger.of("String Longer th".getBytes(StandardCharsets.UTF_8));
        String publicKey = "MDAwMDAwMDAwMTAzMTYzMzc3ODE3NzM4NTg2ODkyNzYwMTE3MTI3Nzk5Nzk3NTQ3MDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAyMjI2NDIyODM1MDk2Mjk5MDAwMDAwMDAwMDYxNDEwMDA2MzM2ODc3MTc1MjYwOTQ3MDU5NTU1NTMzMDc4NDIx";
        String privateKey = "MDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDA4MTYxODk0MDcyMzQ1Nzg0";

        ElGamalPublicKey elGamalPublicKey = KeyConverter.convertFromData(publicKey.getBytes(StandardCharsets.UTF_8));
        LargeInteger elGamalPrivateKey = KeyConverter.convertPrivateFromData(privateKey.getBytes(StandardCharsets.UTF_8));

        ElGamalAlgorithm algorithm = new ElGamalAlgorithm();
        EncryptedBlock encryptedBlock = algorithm.encrypt(elGamalPublicKey, mockData);

        LargeInteger decryptedData = algorithm.decrypt(new ElGamalKeys(elGamalPrivateKey, elGamalPublicKey), encryptedBlock);

        Assert.assertEquals(mockData, decryptedData);
    }

    @Test
    public void encryptString() {
        String mockData = "String Longer than one block to try encrypt and see if we get the same back";

        String publicKey = "MDAwMDAwMDAwMTAzMTYzMzc3ODE3NzM4NTg2ODkyNzYwMTE3MTI3Nzk5Nzk3NTQ3MDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAyMjI2NDIyODM1MDk2Mjk5MDAwMDAwMDAwMDYxNDEwMDA2MzM2ODc3MTc1MjYwOTQ3MDU5NTU1NTMzMDc4NDIx";
        String privateKey = "MDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDA4MTYxODk0MDcyMzQ1Nzg0";

        ElGamalAlgorithm algorithm = new ElGamalAlgorithm();
        byte[] encryptedData = algorithm.encrypt(publicKey, mockData);
        byte[] decryptedData = algorithm.decrypt(privateKey, encryptedData);

        Assert.assertEquals(mockData, new String(decryptedData, StandardCharsets.UTF_8));
    }

    @Test
    public void encryptStringRandomKey() {
        String mockData = "String Longer than one block to try encrypt and see if we get the same back";

        for (int i = 0; i < 100; i++) {
            ElGamalKeys elGamalKeys = ElGamalKeyGenerator.generateKeys();
            byte[] publicKeyBytes = KeyConverter.convertToData(elGamalKeys.getPublicKey());
            byte[] privateKeyBytes = KeyConverter.convertToData(elGamalKeys.getPrivateKey());

            ElGamalAlgorithm algorithm = new ElGamalAlgorithm();
            byte[] encryptedData = algorithm.encrypt(new String(publicKeyBytes, StandardCharsets.UTF_8), mockData);
            byte[] decryptedData = algorithm.decrypt(new String(privateKeyBytes, StandardCharsets.UTF_8), encryptedData);

            Assert.assertEquals(mockData, new String(decryptedData, StandardCharsets.UTF_8));
            System.out.println("Passed test: " + i);
        }
    }
}