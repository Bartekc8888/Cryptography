package elgamal;

import java.math.BigInteger;
import java.util.Random;

import largeinteger.LargeInteger;
import org.junit.Assert;
import org.junit.Test;

public class ElGamalAlgorithmTest {

    @Test
    public void generateElGamalKeys() {
        ElGamalAlgorithm elGamalAlgorithm = new ElGamalAlgorithm();

        ElGamalKeys elGamalKeys = elGamalAlgorithm.generateKeys();

        Assert.assertNotNull(elGamalKeys);
    }

    @Test
    public void generatePrimeNumber() {
        ElGamalAlgorithm elGamalAlgorithm = new ElGamalAlgorithm();

        LargeInteger largeInteger = elGamalAlgorithm.generatePrimeNumber();
        BigInteger probablePrime = new BigInteger(largeInteger.toString());

        Assert.assertTrue(probablePrime.isProbablePrime(100));
    }

    @Test
    public void FermatTest() {
        Random rnd = new Random();

        BigInteger probablePrime = BigInteger.probablePrime(256, rnd);

        Assert.assertTrue(ElGamalAlgorithm.checkIfPassesFermatTest(LargeInteger.of(probablePrime.toString()), 100));
    }

    @Test
    public void MillerRabin() {
        Random rnd = new Random();

        BigInteger probablePrime = BigInteger.probablePrime(256, rnd);

        Assert.assertTrue(ElGamalAlgorithm.checkIfPassesMillerRabin(LargeInteger.of(probablePrime.toString()), 100));
    }

    @Test
    public void primitiveRoot() {
        Random rnd = new Random();
        BigInteger probablePrime = BigInteger.probablePrime(256, rnd);

        LargeInteger largeInteger = LargeInteger.of(probablePrime.toString());
        LargeInteger primitiveRoot = ElGamalAlgorithm.findPrimitiveRoot(largeInteger);

        Assert.assertTrue(primitiveRoot.isGreater(LargeInteger.TWO));
    }
}