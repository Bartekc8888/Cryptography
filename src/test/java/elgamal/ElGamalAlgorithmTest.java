package elgamal;

import java.math.BigInteger;

import largeinteger.LargeInteger;
import org.junit.Assert;
import org.junit.Test;

public class ElGamalAlgorithmTest {

    @Test
    public void generatePrimeNumber() {
        ElGamalAlgorithm elGamalAlgorithm = new ElGamalAlgorithm();

        for (int i = 0; i < 10; i++) {
            LargeInteger largeInteger = elGamalAlgorithm.generatePrimeNumber();
            BigInteger probablePrime = new BigInteger(largeInteger.toString());

            Assert.assertTrue(probablePrime.isProbablePrime(100));
        }
    }
}