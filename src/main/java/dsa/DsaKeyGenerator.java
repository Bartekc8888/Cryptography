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
        LargeInteger primeDivisor = generatePrimeNumber(20);/* LargeInteger.of(new int[]{1,0,5,8,0,4,5,2,3,5,7,1,9,5,2,5,5,9,7,1,0,6,1,6,2,7,5,0,2,1,6,7,4,7,0,8,4,0,6,5,9,4,5,0,2,4,6,8});*/
        LargeInteger primeNumber = generatePrimeNumber(primeDivisor);/* LargeInteger.of(new int[]{9,3,2,5,9,6,0,2,3,3,4,3,1,5,5,9,2,4,7,2,7,1,7,3,7,0,7,2,4,5,7,3,8,5,3,1,4,
                9,7,6,0,6,0,9,6,5,5,8,2,7,6,4,1,5,6,5,9,4,9,5,7,0,7,3,1,9,5,4,9,0,6,0,5,3,1,4,2,5,6,6,9,4,1,7,9,1,0,2,6,2,2,0,8,9,1,3,6,6,0,7,1,7,8,4,3,5,
                9,3,7,6,0,8,5,3,9,5,9,9,7,0,1,0,3,9,3,0,0,5,1,1,3,2,7,4,0,6,0,5,8,1,8,1,7,1,1,2,2,5,4,7,9,2,1,4,3,0,4,4,1,8,8,3,8,6,6,9,0,0,0,5,0,7,5,8,7,
                2,7,0,2,9,0,7,7,0,5,6,0,7,9,9,4,4,6,6,4,8,7,7,4,8,7,6,5,1,7,6,5,9,7,0,3,6,4,3,2,1,2,5,2,5,9,6,2,1,6,0,7,7,0,2,0,7,3,7,8,8,7,5,6,8,2,4,4,4,
                1,3,0,5,4,4,7,2,3,7,2,4,7,6,0,2,1,9,6,3,6,5,1,2,3,2,5,4,1,0,9,9,9,5,1,0,5,4,2,6,5,7,3,2,8,2,5,6,6,2,2,4,5,8,7,4,5,0,9,1,1,0,8,7,1});*/
        LargeInteger generator = generateG(primeNumber, primeDivisor);/* LargeInteger.of(new int[]{0,3,7,0,1,2,4,4,7,6,5,9,7,8,1,9,8,6,6,0,0,2,6,9,8,0,7,3,7,2,9,4,7,7,1,0,8,
                6,3,9,6,7,7,9,0,6,9,4,6,5,0,9,9,9,9,5,9,8,1,8,1,2,7,8,7,0,4,0,3,2,9,4,8,0,6,4,4,6,2,8,4,6,4,6,1,6,3,0,0,0,0,5,8,3,1,3,2,9,4,5,9,3,3,8,0,8,
                8,4,9,2,3,1,9,6,4,9,4,0,5,5,1,0,6,2,7,4,3,8,9,0,9,1,5,8,9,4,5,7,3,4,0,3,8,0,3,0,1,8,1,1,6,7,8,8,4,1,0,8,1,7,6,4,3,7,4,4,8,0,7,3,0,1,6,7,8,
                3,4,2,5,2,4,7,5,0,0,3,8,6,3,2,9,4,9,5,7,5,1,9,4,9,0,7,9,7,7,0,3,5,2,7,0,5,9,0,4,9,8,0,4,7,9,5,1,5,8,7,4,1,3,2,8,5,5,4,8,8,4,3,5,9,9,9,5,0,
                4,0,1,3,8,5,9,4,8,7,9,0,5,3,1,6,5,4,9,7,0,9,4,4,0,6,8,3,5,6,3,4,3,2,5,3,2,1,0,8,9,1,1,8,5,8,1,5,9,0,2,0,4,2,3,5,7,0,2,8,6,0,4,7,1});*/
        LargeInteger privateKey = getDsaPrivateKey(primeDivisor);
        LargeInteger publicKeyPart = computeDsaPublicKeyPart(primeNumber, generator, privateKey);
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

    public static LargeInteger generateG(LargeInteger primeNumber, LargeInteger primeDivisor) {
        LargeInteger h = LargeInteger.createRandom(LargeInteger.TWO, primeNumber.subtract(LargeInteger.ONE));
        boolean found=false;
        LargeInteger g;
        do{
            g = h.modularPower(primeNumber.subtract(LargeInteger.ONE).divide(primeDivisor), primeNumber);
            if(g.isGreater(LargeInteger.ONE))
            {
                found = true;
            }
        } while (!found);
        return g;
    }

    public static LargeInteger generatePrimeNumber(LargeInteger primeDivisor) {
        //int power = getRandomNumberInRange(44, 108);
        boolean foundPrime;
        LargeInteger primeNumber;
        do {
            primeNumber = LargeInteger.createRandom(44);
            primeNumber = primeNumber.multiply(primeDivisor);
            primeNumber = primeNumber.add(LargeInteger.ONE);
            foundPrime = ElGamalKeyGenerator.checkIfPassesMillerRabin(primeNumber, 60);
        }while(!foundPrime);

        return primeNumber;
    }

    private static LargeInteger getDsaPrivateKey(LargeInteger primeDivisor) {
        return LargeInteger.createRandom( LargeInteger.ONE, primeDivisor.subtract(LargeInteger.ONE));
    }

    private static LargeInteger computeDsaPublicKeyPart(LargeInteger primeNumber, LargeInteger generator, LargeInteger privateKey) {
        return generator.modularPower(privateKey, primeNumber);
    }

}
