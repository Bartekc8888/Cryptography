package largeinteger;

import org.junit.Assert;
import org.junit.Test;

public class LargeIntegerTest {

    @Test
    public void multiply() {
        LargeInteger firstInt = LargeInteger.of(new int[]{1, 2, 3, 4, 5, 6});
        LargeInteger secondInt = LargeInteger.of(new int[]{8, 7, 3, 5, 6, 7, 8, 9, 1, 2, 3});
        LargeInteger expectedResult = LargeInteger.of(new int[]{8, 3, 3, 8, 9, 8, 0, 6, 3, 8, 2, 3, 8, 6, 0, 1, 2});

        LargeInteger result = firstInt.multiply(secondInt);

        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void add() {
        LargeInteger firstInt = LargeInteger.of(new int[]{1, 2, 3, 4, 5, 6});
        LargeInteger secondInt = LargeInteger.of(new int[]{8, 7, 3, 5, 6, 7, 8, 9, 1, 2, 3});
        LargeInteger expectedResult = LargeInteger.of(new int[]{9, 9, 6, 9, 1, 4, 9, 9, 1, 2, 3});

        LargeInteger result = firstInt.add(secondInt);

        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void subtract() {
        LargeInteger firstInt = LargeInteger.of(new int[]{8, 7, 3, 5, 6, 7, 8, 9, 1, 2, 3});
        LargeInteger secondInt = LargeInteger.of(new int[]{1, 2, 3, 4, 5, 6});
        LargeInteger expectedResult = LargeInteger.of(new int[]{7, 5, 0, 1, 1, 1, 8, 9, 1, 2, 3});

        LargeInteger result = firstInt.subtract(secondInt);

        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void divide() {
        LargeInteger firstInt = LargeInteger.of(new int[]{8, 7, 3, 5, 6, 7, 8, 9, 1, 2, 3});
        LargeInteger secondInt = LargeInteger.of(new int[]{1, 2, 3, 4, 5, 6});
        LargeInteger expectedResult = LargeInteger.of(new int[]{9, 0, 2, 9, 4});

        LargeInteger result = firstInt.divide(secondInt);

        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void modularPower() {
        LargeInteger firstInt = LargeInteger.of(new int[]{1, 2, 3});
        LargeInteger secondInt = LargeInteger.of(new int[]{1, 1});
        LargeInteger modulo = LargeInteger.of(new int[]{6, 5, 2});
        LargeInteger expectedResult = LargeInteger.of(new int[]{3, 9, 1});

        LargeInteger result = firstInt.modularPower(secondInt, modulo);

        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void isGreaterTrueByLength() {
        LargeInteger firstInt = LargeInteger.of(new int[]{1, 2, 3, 4, 5, 6});
        LargeInteger secondInt = LargeInteger.of(new int[]{8, 7, 3, 5, 6});

        boolean result = firstInt.isGreater(secondInt);

        Assert.assertTrue(result);
    }

    @Test
    public void isGreaterFalseByLength() {
        LargeInteger firstInt = LargeInteger.of(new int[]{1, 2, 3, 4, 5, 6});
        LargeInteger secondInt = LargeInteger.of(new int[]{8, 7, 3, 5, 6, 7, 8});

        boolean result = firstInt.isGreater(secondInt);

        Assert.assertFalse(result);
    }

    @Test
    public void isGreaterTrueByValue() {
        LargeInteger firstInt = LargeInteger.of(new int[]{1, 2, 3, 4, 5, 6});
        LargeInteger secondInt = LargeInteger.of(new int[]{8, 1, 3, 4, 5, 6});

        boolean result = firstInt.isGreater(secondInt);

        Assert.assertTrue(result);
    }

    @Test
    public void isGreaterFalseByValue() {
        LargeInteger firstInt = LargeInteger.of(new int[]{1, 2, 3, 4, 5, 6});
        LargeInteger secondInt = LargeInteger.of(new int[]{8, 2, 3, 4, 5, 6});

        boolean result = firstInt.isGreater(secondInt);

        Assert.assertFalse(result);
    }

    @Test
    public void isGreaterFalse() {
        LargeInteger firstInt = LargeInteger.of(new int[]{1, 2, 3, 4, 5, 6});
        LargeInteger secondInt = LargeInteger.of(new int[]{1, 2, 3, 4, 5, 7});

        boolean result = firstInt.isGreater(secondInt);

        Assert.assertFalse(result);
    }
}