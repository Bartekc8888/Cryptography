package largeinteger;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class LargeInteger {
    public static final LargeInteger ZERO = LargeInteger.of(0);
    public static final LargeInteger ONE = LargeInteger.of(1);
    public static final LargeInteger TWO = LargeInteger.of(2);
    public static final LargeInteger THREE = LargeInteger.of(3);

    private final static int BASE = 10;
    private final int[] digitsArray;

    public LargeInteger(int[] digitsArray) {
        this.digitsArray = digitsArray;
    }

    public static LargeInteger of(int[] digitsArray) {
        return new LargeInteger(digitsArray);
    }

    public static LargeInteger of(int smallNumber) {
        return LargeInteger.of(new int[] { smallNumber });
    }

    public static LargeInteger of(String stringRepresentation) {
        int[] digits = new int[stringRepresentation.length()];

        for (int i = 0; i < stringRepresentation.length(); i++) {
            digits[i] = Character.getNumericValue(stringRepresentation.charAt(stringRepresentation.length() - 1 - i));
        }

        return LargeInteger.of(digits);
    }

    public static LargeInteger createRandom(LargeInteger origin, LargeInteger bound) {
        int[] digits = new int[bound.digitsArray.length];
        LargeInteger integer = LargeInteger.of(digits);

        do {
            for (int i = 0; i < digits.length; i++) {
                digits[i] = Math.abs(ThreadLocalRandom.current().nextInt() % BASE);
            }
        } while (integer.isLessThan(origin) && integer.isGreaterOrEqual(bound));

        return LargeInteger.of(getTrimmedDigitArray(digits));
    }

    public static LargeInteger createRandom(int digitsCount) {
        int[] digits = new int[digitsCount];
        LargeInteger integer = LargeInteger.of(digits);

        for (int i = 0; i < digits.length; i++) {
            digits[i] = Math.abs(ThreadLocalRandom.current().nextInt() % BASE);
        }

        return LargeInteger.of(getTrimmedDigitArray(digits));
    }

    public LargeInteger multiply(LargeInteger otherInteger) {
        int[] resultDigits = new int[digitsArray.length + otherInteger.digitsArray.length];

        for (int i = 0; i < otherInteger.digitsArray.length; i++) {
            int carry = 0;

            for (int j = 0; j < digitsArray.length; j++) {
                int t = (digitsArray[j] * otherInteger.digitsArray[i]) + resultDigits[i + j] + carry;
                carry = t / BASE;
                resultDigits[i + j] = t % BASE;
            }

            resultDigits[i + digitsArray.length] += carry;
        }

        return LargeInteger.of(getTrimmedDigitArray(resultDigits));
    }

    public LargeInteger add(LargeInteger otherInteger) {
        int[] largerNumber = isGreater(otherInteger) ? digitsArray : otherInteger.digitsArray;
        int[] smallerNumber = !isGreater(otherInteger) ? digitsArray : otherInteger.digitsArray;

        int[] resultDigits = new int[largerNumber.length + 1];

        int carry = 0;
        for (int i = 0; i < smallerNumber.length; i++) {
            int sum = smallerNumber[i] + largerNumber[i] + carry;
            resultDigits[i] = sum % BASE;

            carry = sum / BASE;
        }

        // Add remaining digits of larger number
        for (int i = smallerNumber.length; i < largerNumber.length; i++) {
            int sum = largerNumber[i]+ carry;
            resultDigits[i] = sum % BASE;

            carry = sum / BASE;
        }

        // Add remaining carry
        if (carry != 0) {
            resultDigits[largerNumber.length] += carry;
        }

        return LargeInteger.of(getTrimmedDigitArray(resultDigits));
    }

    public LargeInteger subtract(LargeInteger otherInteger) {
        int[] largerNumber = isGreater(otherInteger) ? digitsArray : otherInteger.digitsArray;
        int[] smallerNumber = !isGreater(otherInteger) ? digitsArray : otherInteger.digitsArray;

        int[] resultDigits = new int[largerNumber.length];

        int carry = 0;

        for (int i = 0; i < smallerNumber.length; i++) {
            int sub = largerNumber[i] - smallerNumber[i] - carry;

            if (sub < 0) {
                sub = sub + BASE;
                carry = 1;
            } else {
                carry = 0;
            }

            resultDigits[i] = sub;
        }

        for (int i = smallerNumber.length; i < largerNumber.length; i++) {
            int sub = (largerNumber[i] - carry);

            if (sub < 0) {
                sub = sub + BASE;
                carry = 1;
            } else {
                carry = 0;
            }

            resultDigits[i] = sub;
        }

        return LargeInteger.of(getTrimmedDigitArray(resultDigits));
    }

    public LargeInteger divide(LargeInteger otherInteger) {
        return divideWithReminder(otherInteger).getResult();
    }

    public DivisionResult divideWithReminder(LargeInteger otherInteger) {
        if (equals(otherInteger)) {
            return new DivisionResult(LargeInteger.of(new int[] { 1 }),
                                      LargeInteger.of(new int[] { 0 }));
        }
        if (!isGreater(otherInteger)) {
            return new DivisionResult(LargeInteger.of(new int[] { 0 }),
                                      LargeInteger.of(Arrays.copyOf(digitsArray, digitsArray.length)));
        }

        int[] dividend = Arrays.copyOf(digitsArray, digitsArray.length);
        int[] divisor = Arrays.copyOf(otherInteger.digitsArray, otherInteger.digitsArray.length);

        int[] resultDigits = new int[dividend.length];
        int[] accumulator = new int[divisor.length + 1];
        LargeInteger divisorInteger = LargeInteger.of(divisor);

        int currentIndex = dividend.length - 1;
        while (currentIndex >= 0) {
            shiftDigitsToTheRight(resultDigits);
            shiftDigitsToTheRight(accumulator);
            accumulator[0] = dividend[currentIndex];

            int divisionResult = 0;
            LargeInteger accumulatorInteger = LargeInteger.of(getTrimmedDigitArray(accumulator));
            while (accumulatorInteger.isGreaterOrEqual(divisorInteger)) {
                accumulatorInteger = accumulatorInteger.subtract(divisorInteger);

                divisionResult++;
            }

            accumulator = new int[divisor.length + 1];
            System.arraycopy(accumulatorInteger.digitsArray, 0, accumulator, 0, accumulatorInteger.digitsArray.length);
            resultDigits[0] = divisionResult;

            currentIndex--;
        }

        return new DivisionResult(LargeInteger.of(getTrimmedDigitArray(resultDigits)),
                                  LargeInteger.of(getTrimmedDigitArray(accumulator)));
    }

    public LargeInteger modulo(LargeInteger otherInteger) {
        return divideWithReminder(otherInteger).getReminder();
    }

    public boolean isLessThan(LargeInteger otherInteger) {
        return !isGreater(otherInteger) && !equals(otherInteger);
    }

    public boolean isLessThanOrEqual(LargeInteger otherInteger) {
        return !isGreater(otherInteger) || equals(otherInteger);
    }

    public boolean isGreaterOrEqual(LargeInteger otherInteger) {
        return isGreater(otherInteger) || equals(otherInteger);
    }

    public boolean isGreater(LargeInteger otherInteger) {
        if (this.equals(otherInteger)) {
            return false;
        }

        int significantDigitPosition = getMostSignificantDigitPosition(digitsArray);
        int otherSignificantDigitPosition = getMostSignificantDigitPosition(otherInteger.digitsArray);

        if (significantDigitPosition < otherSignificantDigitPosition) {
            return false;
        } else if (significantDigitPosition > otherSignificantDigitPosition) {
            return true;
        } else {
            for (int i = significantDigitPosition - 1; i >= 0; i--) {
                if (digitsArray[i] > otherInteger.digitsArray[i]) {
                    return true;
                } else if (digitsArray[i] < otherInteger.digitsArray[i]) {
                    return false;
                }
            }
        }

        return false;
    }

    public LargeInteger modularPower(LargeInteger power, LargeInteger modulo) {
        LargeInteger result = LargeInteger.of(new int[] { 1 });
        LargeInteger powerBase = LargeInteger.of(Arrays.copyOf(digitsArray, digitsArray.length));

        powerBase = powerBase.modulo(modulo);
        while (power.isGreater(LargeInteger.ZERO)) {
            if (!power.isEven()) {
                result = (result.multiply(powerBase)).modulo(modulo);
            }

            powerBase = powerBase.multiply(powerBase).modulo(modulo);

            power = power.divide(LargeInteger.TWO);
        }

        return result;
    }

    public LargeInteger power(LargeInteger power) {
        if (power.equals(LargeInteger.ZERO)) {
            return LargeInteger.ONE;
        }

        LargeInteger base = this;

        LargeInteger uneven = LargeInteger.ONE;
        while (power.isGreater(LargeInteger.ONE)) {
            if (power.isEven()) {
                base = base.multiply(base);
                power = power.divide(LargeInteger.TWO);
            } else {
                uneven = base.multiply(uneven);
                base = base.multiply(base);
                power = (power.subtract(LargeInteger.ONE)).divide(LargeInteger.TWO);
            }
        }

        return base.multiply(uneven);
    }

    public boolean isEven() {
        return digitsArray[0] % 2 == 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int i = digitsArray.length - 1; i >= 0; i--) {
            builder.append(digitsArray[i]);
        }

        return builder.toString();
    }

    private static int[] getTrimmedDigitArray(int[] arrayOfDigits) {
        int pos = getMostSignificantDigitPosition(arrayOfDigits);

        int[] resultArray = new int[pos];
        System.arraycopy(arrayOfDigits, 0, resultArray, 0, pos);

        return resultArray;
    }

    private static int getMostSignificantDigitPosition(int[] arrayOfDigits) {
        int pos = 0;

        for (int i = 0; i < arrayOfDigits.length; i++) {
            if (arrayOfDigits[i] != 0) {
                pos = i;
            }
        }
        pos++;

        return pos;
    }

    private void shiftDigitsToTheRight(int[] arrayOfDigits) {
        if (arrayOfDigits.length - 1 >= 0) {
            System.arraycopy(arrayOfDigits, 0, arrayOfDigits, 1, arrayOfDigits.length - 1);
        }

        arrayOfDigits[0] = 0;
    }
}
