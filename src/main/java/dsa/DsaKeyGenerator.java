package dsa;

import dsa.DsaKeys;
import dsa.DsaPublicKey;
import java.util.Random;

import elgamal.ElGamalKeyGenerator;
import largeinteger.LargeInteger;

import java.util.Random;

public class DsaKeyGenerator {
    private static final LargeInteger[] aValues = {LargeInteger.TWO, LargeInteger.THREE};

    public static DsaKeys generateKeys() {
        LargeInteger primeDivisor = generatePrimeNumber(20);
        System.out.println("primeD: " + primeDivisor);
        LargeInteger primeNumber = generatePrimeNumber(primeDivisor);
        System.out.println("primeN: " + primeNumber);
        LargeInteger generator = LargeInteger.TWO;
        System.out.println("generat: " + generator);
        LargeInteger privateKey = getDsaPrivateKey(primeDivisor);
        System.out.println("private: " + privateKey);
        LargeInteger publicKeyPart = computeDsaPublicKeyPart(primeNumber, generator, privateKey);
        System.out.println("public: " + publicKeyPart);
        return new DsaKeys(privateKey, new DsaPublicKey(primeNumber, primeDivisor, generator, publicKeyPart));
    }

    public static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static LargeInteger generatePrimeNumber(int bitLength) {
        boolean foundPrime;
        LargeInteger potentialPrime;

        do {
            potentialPrime = LargeInteger.createRandom(bitLength);
            foundPrime = ElGamalKeyGenerator.checkIfPassesMillerRabin(potentialPrime, 60);
        } while (!foundPrime);


        return potentialPrime;
    }

    public static LargeInteger generatePrimeNumber(LargeInteger primeDivisor) {
       // int power = getRandomNumberInRange(44, 108);
        LargeInteger primeNumber = generatePrimeNumber(108);
        System.out.println("primeFirst: " + primeNumber);
        primeNumber.multiply(primeDivisor);
        System.out.println("multiplied: " + primeNumber);
        primeNumber.add(LargeInteger.ONE);
        System.out.println("added: " + primeNumber);

        return primeNumber;
    }

    private static LargeInteger getDsaPrivateKey(LargeInteger primeDivisor) {
        return LargeInteger.createRandom( LargeInteger.ONE, primeDivisor.subtract(LargeInteger.ONE));
    }

    private static LargeInteger computeDsaPublicKeyPart(LargeInteger primeNumber, LargeInteger generator, LargeInteger privateKey) {
        return generator.modularPower(privateKey, primeNumber);
    }

}
