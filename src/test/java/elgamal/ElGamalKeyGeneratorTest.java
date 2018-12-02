package elgamal;

import java.math.BigInteger;
import java.util.Random;

import largeinteger.LargeInteger;
import org.junit.Assert;
import org.junit.Test;

public class ElGamalKeyGeneratorTest {

    @Test
    public void generateElGamalKeys() {
        ElGamalKeys elGamalKeys = ElGamalKeyGenerator.generateKeys();

        Assert.assertNotNull(elGamalKeys);
    }

    @Test
    public void generatePrimeNumber() {
        LargeInteger largeInteger = ElGamalKeyGenerator.generatePrimeNumber();
        BigInteger probablePrime = new BigInteger(largeInteger.toString());

        Assert.assertTrue(probablePrime.isProbablePrime(100));
    }

    @Test
    public void FermatTest() {
        Random rnd = new Random();

        BigInteger probablePrime = BigInteger.probablePrime(256, rnd);

        Assert.assertTrue(ElGamalKeyGenerator.checkIfPassesFermatTest(LargeInteger.of(probablePrime.toString()), 100));
    }

    @Test
    public void MillerRabin() {
        Random rnd = new Random();

        BigInteger probablePrime = BigInteger.probablePrime(256, rnd);

        Assert.assertTrue(ElGamalKeyGenerator.checkIfPassesMillerRabin(LargeInteger.of(probablePrime.toString()), 100));
    }

    @Test
    public void primitiveRoot() {
        Random rnd = new Random();
        BigInteger probablePrime = BigInteger.probablePrime(256, rnd);

        LargeInteger largeInteger = LargeInteger.of(probablePrime.toString());
        LargeInteger primitiveRoot = ElGamalKeyGenerator.findPrimitiveRoot(largeInteger);

        Assert.assertTrue(primitiveRoot.isGreater(LargeInteger.TWO));
    }
}