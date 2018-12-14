package dsa;

import java.math.BigInteger;
import java.util.Random;

public class DsaKeyGenerator {
    private static final BigInteger[] aValues = {BigInteger.TWO, BigInteger.ONE.add(BigInteger.TWO)};

    public static DsaKeys generateKeys() {
        BigInteger primeDivisor = generatePrimeNumber(20);/* LargeInteger.of(new int[]{1,0,5,8,0,4,5,2,3,5,7,1,9,5,2,5,5,9,7,1,0,6,1,6,2,7,5,0,2,1,6,7,4,7,0,8,4,0,6,5,9,4,5,0,2,4,6,8});*/
        BigInteger primeNumber = generatePrimeNumber(primeDivisor);/* LargeInteger.of(new int[]{9,3,2,5,9,6,0,2,3,3,4,3,1,5,5,9,2,4,7,2,7,1,7,3,7,0,7,2,4,5,7,3,8,5,3,1,4,
                9,7,6,0,6,0,9,6,5,5,8,2,7,6,4,1,5,6,5,9,4,9,5,7,0,7,3,1,9,5,4,9,0,6,0,5,3,1,4,2,5,6,6,9,4,1,7,9,1,0,2,6,2,2,0,8,9,1,3,6,6,0,7,1,7,8,4,3,5,
                9,3,7,6,0,8,5,3,9,5,9,9,7,0,1,0,3,9,3,0,0,5,1,1,3,2,7,4,0,6,0,5,8,1,8,1,7,1,1,2,2,5,4,7,9,2,1,4,3,0,4,4,1,8,8,3,8,6,6,9,0,0,0,5,0,7,5,8,7,
                2,7,0,2,9,0,7,7,0,5,6,0,7,9,9,4,4,6,6,4,8,7,7,4,8,7,6,5,1,7,6,5,9,7,0,3,6,4,3,2,1,2,5,2,5,9,6,2,1,6,0,7,7,0,2,0,7,3,7,8,8,7,5,6,8,2,4,4,4,
                1,3,0,5,4,4,7,2,3,7,2,4,7,6,0,2,1,9,6,3,6,5,1,2,3,2,5,4,1,0,9,9,9,5,1,0,5,4,2,6,5,7,3,2,8,2,5,6,6,2,2,4,5,8,7,4,5,0,9,1,1,0,8,7,1});*/
        BigInteger generator = generateG(primeNumber, primeDivisor);/* LargeInteger.of(new int[]{0,3,7,0,1,2,4,4,7,6,5,9,7,8,1,9,8,6,6,0,0,2,6,9,8,0,7,3,7,2,9,4,7,7,1,0,8,
                6,3,9,6,7,7,9,0,6,9,4,6,5,0,9,9,9,9,5,9,8,1,8,1,2,7,8,7,0,4,0,3,2,9,4,8,0,6,4,4,6,2,8,4,6,4,6,1,6,3,0,0,0,0,5,8,3,1,3,2,9,4,5,9,3,3,8,0,8,
                8,4,9,2,3,1,9,6,4,9,4,0,5,5,1,0,6,2,7,4,3,8,9,0,9,1,5,8,9,4,5,7,3,4,0,3,8,0,3,0,1,8,1,1,6,7,8,8,4,1,0,8,1,7,6,4,3,7,4,4,8,0,7,3,0,1,6,7,8,
                3,4,2,5,2,4,7,5,0,0,3,8,6,3,2,9,4,9,5,7,5,1,9,4,9,0,7,9,7,7,0,3,5,2,7,0,5,9,0,4,9,8,0,4,7,9,5,1,5,8,7,4,1,3,2,8,5,5,4,8,8,4,3,5,9,9,9,5,0,
                4,0,1,3,8,5,9,4,8,7,9,0,5,3,1,6,5,4,9,7,0,9,4,4,0,6,8,3,5,6,3,4,3,2,5,3,2,1,0,8,9,1,1,8,5,8,1,5,9,0,2,0,4,2,3,5,7,0,2,8,6,0,4,7,1});*/
        BigInteger privateKey = getDsaPrivateKey(primeDivisor);
        BigInteger publicKeyPart = computeDsaPublicKeyPart(primeNumber, generator, privateKey);
        return new DsaKeys(privateKey, new DsaPublicKey(primeNumber, primeDivisor, generator, publicKeyPart));
    }

    public static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static BigInteger generatePrimeNumber(int bitLength) {
        boolean foundPrime;
        BigInteger k;
        do {
            Random random = new Random();
            do {
                k = new BigInteger(bitLength, random);
            } while (k.compareTo(BigInteger.ONE) <= 0);
            foundPrime = checkIfPassesMillerRabin(k, 60);
        } while (!foundPrime);

        return k;
    }

    private static boolean checkIfPassesMillerRabin(BigInteger valueToTest, int numberOfTestsToApply) {
        BigInteger d = valueToTest.subtract(BigInteger.ONE);
        int s = 0;
        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            s++;
            d = d.divide(BigInteger.TWO);
        }
        for (int i = 0; i < aValues.length; i++) {
            BigInteger a = aValues[i];
            boolean r = testPr(valueToTest, a, s, d);
            if (!r) {
                return false;
            }
        }
        return true;
    }

    public static boolean testPr(BigInteger n, BigInteger a, int s, BigInteger d) {
        for (int i = 0; i < s; i++) {
            BigInteger exp = BigInteger.TWO.pow(i);
            exp = exp.multiply(d);
            BigInteger res = a.modPow(exp, n);
            if (res.equals(n.subtract(BigInteger.ONE)) || res.equals(BigInteger.ONE)) {
                return true;
            }
        }

        return false;
    }

    public static BigInteger generateG(BigInteger primeNumber, BigInteger primeDivisor) {
        BigInteger h;// = BigInteger.createRandom(LargeInteger.TWO, primeNumber.subtract(BigInteger.ONE));
        Random random = new Random();
        do {
            h = new BigInteger(primeNumber.subtract(BigInteger.ONE).bitLength(), random);
        } while (h.compareTo(BigInteger.TWO) <= 0);
        boolean found = false;
        BigInteger g;
        do {
            g = h.modPow(primeNumber.subtract(BigInteger.ONE).divide(primeDivisor), primeNumber);
        if (g.compareTo(BigInteger.ONE) > 0) {
                found = true;
            }
        } while (!found);
        return g;
    }

    public static BigInteger generatePrimeNumber(BigInteger primeDivisor) {
        //int power = getRandomNumberInRange(44, 108);
        boolean foundPrime;
        BigInteger primeNumber;
        do {
            Random random = new Random();
            do {
                primeNumber = new BigInteger(44, random);
            } while (primeNumber.compareTo(BigInteger.ONE) <= 0);
            primeNumber = primeNumber.multiply(primeDivisor);
            primeNumber = primeNumber.add(BigInteger.ONE);
            foundPrime = checkIfPassesMillerRabin(primeNumber, 60);
        } while (!foundPrime);

        return primeNumber;
    }

    private static BigInteger getDsaPrivateKey(BigInteger primeDivisor) {
        BigInteger k;
        Random random = new Random();
        do {
            k = new BigInteger(primeDivisor.subtract(BigInteger.ONE).bitLength(), random);
        } while (k.compareTo(BigInteger.ONE) <= 0);
        return k;
    }

    private static BigInteger computeDsaPublicKeyPart(BigInteger primeNumber, BigInteger generator, BigInteger privateKey) {
        return generator.modPow(privateKey, primeNumber);
    }

}
