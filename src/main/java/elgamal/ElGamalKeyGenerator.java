package elgamal;

import largeinteger.LargeInteger;

public class ElGamalKeyGenerator {

    private static final LargeInteger[] aValues = { LargeInteger.TWO, LargeInteger.THREE };

    public static ElGamalKeys generateKeys() {
        LargeInteger primeNumber = generatePrimeNumber();
        LargeInteger primitiveRoot = findPrimitiveRoot(primeNumber);
        LargeInteger privateKey = getPrivateKey(primeNumber);
        LargeInteger publicKeyPart = computePublicKeyPart(primeNumber, primitiveRoot, privateKey);

        return new ElGamalKeys(privateKey, new ElGamalPublicKey(primeNumber, primitiveRoot, publicKeyPart));
    }

    public static LargeInteger generatePrimeNumber() {
        boolean foundPrime;
        LargeInteger potentialPrime;

        do {
            potentialPrime = LargeInteger.createRandom(16);
            foundPrime = checkIfPassesMillerRabin(potentialPrime, 60);
        } while (!foundPrime);


        return potentialPrime;
    }

    public static boolean checkIfPassesFermatTest(LargeInteger valueToTest, int numberOfTestsToApply) {
        if (valueToTest.isLessThan(LargeInteger.of(4)) || numberOfTestsToApply < 1) {
            throw new IllegalArgumentException("Invalid argument passed to Fermat test");
        }

        if (valueToTest.isEven()) {
            return false;
        }

        for (int i = 0; i < numberOfTestsToApply; i++)
        {
            LargeInteger a = LargeInteger.createRandom(LargeInteger.of(2), valueToTest);
            a = a.modularPower(valueToTest.subtract(LargeInteger.ONE), valueToTest);

            if (!a.equals(LargeInteger.ONE))
                return false;
        }

        return true;
    }

    public static boolean checkIfPassesMillerRabin(LargeInteger valueToTest, int numberOfTestsToApply) {
        LargeInteger d = valueToTest.subtract(LargeInteger.ONE);
        int s = 0;
        while (d.modulo(LargeInteger.TWO).equals(LargeInteger.ZERO)) {
            s++;
            d = d.divide(LargeInteger.TWO);
        }
        for (int i = 0; i < aValues.length; i++) {
            LargeInteger a = aValues[i];
            boolean r = testPr(valueToTest, a, s, d);
            if (!r) {
                return false;
            }
        }
        return true;
    }

    public static boolean testPr(LargeInteger n, LargeInteger a, int s, LargeInteger d) {
        for (int i = 0; i < s; i++) {
            LargeInteger exp = LargeInteger.TWO.power(LargeInteger.of(i));
            exp = exp.multiply(d);
            LargeInteger res = a.modularPower(exp, n);
            if (res.equals(n.subtract(LargeInteger.ONE)) || res.equals(LargeInteger.ONE)) {
                return true;
            }
        }

        return false;
    }

    public static LargeInteger findPrimitiveRoot(LargeInteger primeNumber) {
        if (primeNumber.equals(LargeInteger.TWO)) {
            return LargeInteger.ONE;
        }

        LargeInteger p1 = LargeInteger.TWO;
        LargeInteger primeMinusOne = primeNumber.subtract(LargeInteger.ONE);
        LargeInteger p2 = primeMinusOne.divide(p1);

        while(true) {
            LargeInteger g = LargeInteger.createRandom( LargeInteger.TWO, primeMinusOne);
            if (!g.modularPower(primeMinusOne.divide(p1), primeNumber).equals(LargeInteger.ONE)) {
                if  (!g.modularPower(primeMinusOne.divide(p2), primeMinusOne).equals(LargeInteger.ONE)) {
                    return g;
                }
            }
        }
    }

    private static LargeInteger getPrivateKey(LargeInteger primeNumber) {
        return LargeInteger.createRandom( LargeInteger.TWO, primeNumber.subtract(LargeInteger.ONE));
    }

    private static LargeInteger computePublicKeyPart(LargeInteger primeNumber, LargeInteger generator, LargeInteger privateKey) {
        return generator.modularPower(privateKey, primeNumber);
    }
}
