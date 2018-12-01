package elgamal;

import largeinteger.LargeInteger;

public class ElGamalAlgorithm {


    public ElGamalAlgorithm() {

    }

    public LargeInteger generatePrimeNumber() {
        boolean foundPrime;
        LargeInteger potentialPrime;

        do {
            potentialPrime = LargeInteger.createRandom(20);
            foundPrime = checkIfPassesFermatTest(potentialPrime, 60);
        } while (!foundPrime);


        return potentialPrime;
    }

    private boolean checkIfPassesFermatTest(LargeInteger valueToTest, int numberOfTestsToApply) {
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
}
