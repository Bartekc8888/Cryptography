package elgamal;

import java.math.BigInteger;
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
        LargeInteger mockData = LargeInteger.of("String Longer t".getBytes(StandardCharsets.UTF_8));
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
    public void wholeAlgorithmTest() {
        LargeInteger mockData = LargeInteger.of("String Longer th".getBytes(StandardCharsets.UTF_8));
        String publicKey = "MDAwMDAwMDAwMTAzMTYzMzc3ODE3NzM4NTg2ODkyNzYwMTE3MTI3Nzk5Nzk3NTQ3MDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAyMjI2NDIyODM1MDk2Mjk5MDAwMDAwMDAwMDYxNDEwMDA2MzM2ODc3MTc1MjYwOTQ3MDU5NTU1NTMzMDc4NDIx";
        String privateKey = "MDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDA4MTYxODk0MDcyMzQ1Nzg0";

        ElGamalPublicKey elGamalPublicKey = KeyConverter.convertFromData(publicKey.getBytes(StandardCharsets.UTF_8));
        LargeInteger elGamalPrivateKey = KeyConverter.convertPrivateFromData(privateKey.getBytes(StandardCharsets.UTF_8));


        LargeInteger randomNumber = LargeInteger.of("123456");

        BigInteger bigPrime = new BigInteger(elGamalPublicKey.getPrimeNumber().toString());
        BigInteger bigRandom = new BigInteger(randomNumber.toString());
        Assert.assertTrue(bigPrime.isProbablePrime(200));

        LargeInteger c1 = elGamalPublicKey.getGenerator().modularPower(randomNumber, elGamalPublicKey.getPrimeNumber());
        BigInteger expectedC1 = new BigInteger(elGamalPublicKey.getGenerator().toString()).modPow(bigRandom, bigPrime);
        Assert.assertEquals(expectedC1.toString(), c1.toString());

        LargeInteger dataModulo = mockData.modulo(elGamalPublicKey.getPrimeNumber());
        BigInteger expectedDataModulo = new BigInteger(mockData.toString()).mod(bigPrime);
        Assert.assertEquals(expectedDataModulo.toString(), dataModulo.toString());

        LargeInteger publicPartToRandomPower = elGamalPublicKey.getPublicKeyPart().modularPower(randomNumber,
                                                                                                elGamalPublicKey.getPrimeNumber());
        BigInteger expectedPublicPartToRandomPower = new BigInteger(elGamalPublicKey.getPublicKeyPart().toString()).modPow(bigRandom,
                                                                                                                           bigPrime);
        Assert.assertEquals(expectedPublicPartToRandomPower.toString(), publicPartToRandomPower.toString());

        LargeInteger c2 = dataModulo.multiply(publicPartToRandomPower)
                                    .modulo(elGamalPublicKey.getPrimeNumber());
        BigInteger expectedC2 = expectedDataModulo.multiply(expectedPublicPartToRandomPower)
                                    .mod(bigPrime);
        Assert.assertEquals(expectedC2.toString(), c2.toString());

        // decryption
        LargeInteger primeNumber = elGamalPublicKey.getPrimeNumber();

        LargeInteger randomModular = c1.modularPower(elGamalPrivateKey, primeNumber);
        BigInteger expectedRandomModular = expectedC1.modPow(new BigInteger(elGamalPrivateKey.toString()), bigPrime);
        Assert.assertEquals(expectedRandomModular.toString(), randomModular.toString());

        LargeInteger multiplicativeInverse = randomModular.multiplicativeInverse(primeNumber);
        BigInteger expectedMultiplicativeInverse = expectedRandomModular.modInverse(bigPrime);
        Assert.assertEquals(expectedMultiplicativeInverse.toString(), multiplicativeInverse.toString());

        LargeInteger decoded = multiplicativeInverse.multiply(c2).modulo(primeNumber);
        BigInteger expectedDecoded = expectedMultiplicativeInverse.multiply(expectedC2).mod(bigPrime);
        Assert.assertEquals(expectedDecoded.toString(), decoded.toString());

        Assert.assertEquals(mockData, decoded);
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

        ElGamalKeys elGamalKeys = ElGamalKeyGenerator.generateKeys();
        byte[] publicKeyBytes = KeyConverter.convertToData(elGamalKeys.getPublicKey());
        byte[] privateKeyBytes = KeyConverter.convertToData(elGamalKeys.getPrivateKey());

        ElGamalAlgorithm algorithm = new ElGamalAlgorithm();
        byte[] encryptedData = algorithm.encrypt(new String(publicKeyBytes, StandardCharsets.UTF_8), mockData);
        byte[] decryptedData = algorithm.decrypt(new String(privateKeyBytes, StandardCharsets.UTF_8), encryptedData);

        Assert.assertEquals(mockData, new String(decryptedData, StandardCharsets.UTF_8));
    }
}