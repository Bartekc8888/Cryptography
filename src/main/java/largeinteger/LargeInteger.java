package largeinteger;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "of")
public class LargeInteger {
    private final static int BASE = 10;
    private final int[] digitsArray;

    public static LargeInteger of(int smallNumber) {
        return LargeInteger.of(new int[] { smallNumber });
    }

    public static LargeInteger createRandom(LargeInteger origin, LargeInteger bound) {
        int[] digits = new int[bound.digitsArray.length];
        LargeInteger integer = LargeInteger.of(digits);

        do {
            for (int i = 0; i < digits.length; i++) {
                digits[i] = ThreadLocalRandom.current().nextInt() % BASE;
            }
        } while (integer.isLessThan(origin) && integer.isGreater(bound));

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
        return divide(otherInteger, false);
    }

    public LargeInteger divide(LargeInteger otherInteger, boolean isModulo) {
        if (equals(otherInteger)) {
            return LargeInteger.of(new int[] {1});
        }
        if (!isGreater(otherInteger)) {
            if (isModulo) {
                return LargeInteger.of(Arrays.copyOf(digitsArray, digitsArray.length));
            } else {
                return LargeInteger.of(new int[] { 0 });
            }
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
            LargeInteger accumulatorInteger = LargeInteger.of(accumulator);
            while (accumulatorInteger.isGreaterOrEqual(divisorInteger)) {
                accumulatorInteger = accumulatorInteger.subtract(divisorInteger);

                divisionResult++;
            }

            accumulator = new int[divisor.length + 1];
            System.arraycopy(accumulatorInteger.digitsArray, 0, accumulator, 0, accumulatorInteger.digitsArray.length);
            resultDigits[0] = divisionResult;

            currentIndex--;
        }

        if (!isModulo) {
            return LargeInteger.of(getTrimmedDigitArray(resultDigits));
        } else {
            return LargeInteger.of(getTrimmedDigitArray(accumulator));
        }
    }

    public LargeInteger modulo(LargeInteger otherInteger) {
        return divide(otherInteger, true);
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

        LargeInteger counterInt = LargeInteger.of(new int[] { 0 });

        while (!counterInt.isGreaterOrEqual(power)) {
            result = powerBase.multiply(result).modulo(modulo);

            counterInt = counterInt.add(LargeInteger.of(new int[] { 1 }));
        }

        return result;
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
