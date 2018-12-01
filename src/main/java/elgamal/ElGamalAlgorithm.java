package elgamal;

import largeinteger.LargeInteger;

public class ElGamalAlgorithm {

    private static final LargeInteger[] aValues = { LargeInteger.TWO, LargeInteger.THREE };

    public ElGamalAlgorithm() {

    }

    public LargeInteger generatePrimeNumber() {
        boolean foundPrime;
        LargeInteger potentialPrime;

        do {
            potentialPrime = LargeInteger.createRandom(32);
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
}
