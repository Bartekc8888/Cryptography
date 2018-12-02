package largeinteger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import static java.lang.Byte.toUnsignedInt;

@EqualsAndHashCode
public class LargeInteger {
    public static final LargeInteger ZERO = LargeInteger.of(0, IntegerBase.BASE_256);
    public static final LargeInteger ONE = LargeInteger.of(1, IntegerBase.BASE_256);
    public static final LargeInteger TWO = LargeInteger.of(2, IntegerBase.BASE_256);
    public static final LargeInteger THREE = LargeInteger.of(3, IntegerBase.BASE_256);

    @Getter
    private final IntegerBase BASE;
    private final int[] digitsArray;

    private LargeInteger(int[] digitsArray) {
        this(digitsArray, IntegerBase.BASE_10);
    }

    private LargeInteger(int[] digitsArray, IntegerBase base) {
        this.digitsArray = digitsArray;
        this.BASE = base;
    }

    public static LargeInteger of(byte[] digitsArray) {
        int[] intArray = new int[digitsArray.length];
        for (int i = 0; i < digitsArray.length; i++) {
            intArray[i] = toUnsignedInt(digitsArray[i]);
        }

        LargeInteger largeInteger = new LargeInteger(intArray, IntegerBase.BASE_256);
        return fromBase10ToBase256(largeInteger);
    }

    public static LargeInteger of(int[] digitsArray) {
        LargeInteger largeInteger = new LargeInteger(digitsArray, IntegerBase.BASE_10);
        return fromBase10ToBase256(largeInteger);
    }

    public static LargeInteger of(int[] digitsArray, IntegerBase base) {
        return new LargeInteger(digitsArray, base);
    }

    public static LargeInteger of(int smallNumber) {
        return LargeInteger.of(splitToDigits(smallNumber, IntegerBase.BASE_10));
    }

    private static LargeInteger of(int smallNumber, IntegerBase base) {
        return LargeInteger.of(splitToDigits(smallNumber, base), base);
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
                digits[i] = Math.abs(ThreadLocalRandom.current().nextInt() % IntegerBase.BASE_10.getBase());
            }
        } while (integer.isLessThan(origin) && integer.isGreaterOrEqual(bound));

        return LargeInteger.of(getTrimmedDigitArray(digits));
    }

    public static LargeInteger createRandom(int numberOfBytes) {
        int[] digits = new int[numberOfBytes];

        for (int i = 0; i < digits.length; i++) {
            digits[i] = Math.abs(ThreadLocalRandom.current().nextInt() % IntegerBase.BASE_256.getBase());
        }

        return LargeInteger.of(getTrimmedDigitArray(digits), IntegerBase.BASE_256);
    }

    public static LargeInteger fromBase10ToBase256(LargeInteger decDigits) {
        if (decDigits.getBASE() == IntegerBase.BASE_256) {
            return decDigits;
        }

        List<Integer> newBaseDigits = new ArrayList<>();
        LargeInteger baseDivisor = LargeInteger.of(IntegerBase.BASE_256.getBase(), IntegerBase.BASE_10);

        divideByNewBase(decDigits, newBaseDigits, baseDivisor);

        int[] digitsArray = newBaseDigits.stream().mapToInt(i -> i).toArray();
        return LargeInteger.of(digitsArray, IntegerBase.BASE_256);
    }

    public static LargeInteger fromBase256ToBase10(LargeInteger base256Digits) {
        if (base256Digits.getBASE() == IntegerBase.BASE_10) {
            return base256Digits;
        }

        List<Integer> newBaseDigits = new ArrayList<>();
        LargeInteger baseDivisor = LargeInteger.of(IntegerBase.BASE_10.getBase(), IntegerBase.BASE_256);

        divideByNewBase(base256Digits, newBaseDigits, baseDivisor);

        int[] digitsArray = newBaseDigits.stream().mapToInt(i -> i).toArray();
        return LargeInteger.of(digitsArray, IntegerBase.BASE_10);
    }

    private static void divideByNewBase(LargeInteger oldBaseDigits, List<Integer> newBaseDigits, LargeInteger baseDivisor) {
        LargeInteger zeroInteger = LargeInteger.of(0, oldBaseDigits.BASE);
        DivisionResult divisionResult;

        do {
            divisionResult = oldBaseDigits.divideWithReminder(baseDivisor);
            newBaseDigits.add(toInt(divisionResult.getReminder()));

            oldBaseDigits = divisionResult.getResult();
        } while (oldBaseDigits.isGreater(zeroInteger));
    }

    private static int[] splitToDigits(int smallNumber, IntegerBase base) {
        List<Integer> baseDigits = new ArrayList<>();

        do {
            int reminder = smallNumber % base.getBase();
            baseDigits.add(reminder);

            smallNumber = smallNumber / base.getBase();
        } while (smallNumber > 0);

        return baseDigits.stream().mapToInt(i -> i).toArray();
    }

    public LargeInteger multiply(LargeInteger otherInteger) {
        int[] resultDigits = new int[digitsArray.length + otherInteger.digitsArray.length];

        for (int i = 0; i < otherInteger.digitsArray.length; i++) {
            int carry = 0;

            for (int j = 0; j < digitsArray.length; j++) {
                int t = (digitsArray[j] * otherInteger.digitsArray[i]) + resultDigits[i + j] + carry;
                carry = t / BASE.getBase();
                resultDigits[i + j] = t % BASE.getBase();
            }

            resultDigits[i + digitsArray.length] += carry;
        }

        return LargeInteger.of(getTrimmedDigitArray(resultDigits), BASE);
    }

    public LargeInteger add(LargeInteger otherInteger) {
        int[] largerNumber = isGreater(otherInteger) ? digitsArray : otherInteger.digitsArray;
        int[] smallerNumber = !isGreater(otherInteger) ? digitsArray : otherInteger.digitsArray;

        int[] resultDigits = new int[largerNumber.length + 1];

        int carry = 0;
        for (int i = 0; i < smallerNumber.length; i++) {
            int sum = smallerNumber[i] + largerNumber[i] + carry;
            resultDigits[i] = sum % BASE.getBase();

            carry = sum / BASE.getBase();
        }

        // Add remaining digits of larger number
        for (int i = smallerNumber.length; i < largerNumber.length; i++) {
            int sum = largerNumber[i]+ carry;
            resultDigits[i] = sum % BASE.getBase();

            carry = sum / BASE.getBase();
        }

        // Add remaining carry
        if (carry != 0) {
            resultDigits[largerNumber.length] += carry;
        }

        return LargeInteger.of(getTrimmedDigitArray(resultDigits), BASE);
    }

    public LargeInteger subtract(LargeInteger otherInteger) {
        int[] largerNumber = isGreater(otherInteger) ? digitsArray : otherInteger.digitsArray;
        int[] smallerNumber = !isGreater(otherInteger) ? digitsArray : otherInteger.digitsArray;

        int[] resultDigits = new int[largerNumber.length];

        int carry = 0;

        for (int i = 0; i < smallerNumber.length; i++) {
            int sub = largerNumber[i] - smallerNumber[i] - carry;

            if (sub < 0) {
                sub = sub + BASE.getBase();
                carry = 1;
            } else {
                carry = 0;
            }

            resultDigits[i] = sub;
        }

        for (int i = smallerNumber.length; i < largerNumber.length; i++) {
            int sub = (largerNumber[i] - carry);

            if (sub < 0) {
                sub = sub + BASE.getBase();
                carry = 1;
            } else {
                carry = 0;
            }

            resultDigits[i] = sub;
        }

        return LargeInteger.of(getTrimmedDigitArray(resultDigits), BASE);
    }

    public LargeInteger divide(LargeInteger otherInteger) {
        return divideWithReminder(otherInteger).getResult();
    }

    public DivisionResult divideWithReminder(LargeInteger otherInteger) {
        if (equals(otherInteger)) {
            return new DivisionResult(LargeInteger.of(1, BASE),
                                      LargeInteger.of(0, BASE));
        }
        if (!isGreater(otherInteger)) {
            return new DivisionResult(LargeInteger.of(0, BASE),
                                      this);
        }

        int[] dividend = Arrays.copyOf(digitsArray, digitsArray.length);
        int[] divisor = Arrays.copyOf(otherInteger.digitsArray, otherInteger.digitsArray.length);

        int[] resultDigits = new int[dividend.length];
        int[] accumulator = new int[divisor.length + 1];
        LargeInteger divisorInteger = LargeInteger.of(divisor, BASE);

        int currentIndex = dividend.length - 1;
        while (currentIndex >= 0) {
            shiftDigitsToTheRight(resultDigits);
            shiftDigitsToTheRight(accumulator);
            accumulator[0] = dividend[currentIndex];

            int divisionResult = 0;
            LargeInteger accumulatorInteger = LargeInteger.of(getTrimmedDigitArray(accumulator), BASE);
            while (accumulatorInteger.isGreaterOrEqual(divisorInteger)) {
                accumulatorInteger = accumulatorInteger.subtract(divisorInteger);

                divisionResult++;
            }

            accumulator = new int[divisor.length + 1];
            System.arraycopy(accumulatorInteger.digitsArray, 0, accumulator, 0, accumulatorInteger.digitsArray.length);
            resultDigits[0] = divisionResult;

            currentIndex--;
        }

        return new DivisionResult(LargeInteger.of(getTrimmedDigitArray(resultDigits), BASE),
                                  LargeInteger.of(getTrimmedDigitArray(accumulator), BASE));
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
        if (BASE != otherInteger.BASE) {
            throw new InputMismatchException("Integer bases are different");
        }
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
        LargeInteger result = LargeInteger.of(1, BASE);
        LargeInteger powerBase = this;

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
        if (BASE == IntegerBase.BASE_256) {
            return toStringFromBase256();
        }

        StringBuilder builder = new StringBuilder();

        for (int i = digitsArray.length - 1; i >= 0; i--) {
            builder.append(digitsArray[i]);
        }

        return builder.toString();
    }

    private String toStringFromBase256() {
        StringBuilder builder = new StringBuilder();
        LargeInteger largeInteger = fromBase256ToBase10(this);

        for (int i = largeInteger.digitsArray.length - 1; i >= 0; i--) {
            builder.append(largeInteger.digitsArray[i]);
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

    private static int toInt(LargeInteger integer) {
        int value = 0;

        for (int i = integer.digitsArray.length - 1; i >= 0; i--) {
            value *= integer.BASE.getBase();
            value += integer.digitsArray[i];
        }

        return value;
    }

    public void toByteArray(byte[] byteArray) {
        if (byteArray.length < digitsArray.length) {
            throw new IllegalArgumentException("Array is to short");
        }

        for (int i = 0; i < digitsArray.length; i++) {
            byteArray[i] = (byte) digitsArray[i];
        }
    }
}
