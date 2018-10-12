package aes;

class RijndaelDefinitions {
    private static final int GaloisFieldSize = 256;
    private static final int ReducingPolynomial = 0b1_0001_1011; // x^8 + x^4 + x^3 + x + 1

    private final byte[] substitutionBox;
    private final byte[] multiplyBy2LookupTable;
    private final byte[] multiplyBy3LookupTable;
    private final byte[] multiplicativeInverse;
    private final byte[][] mixRows = {{2, 3, 1, 1},
                                      {1, 2, 3, 1},
                                      {1, 1, 2, 3},
                                      {3, 1, 1, 2}};

    RijndaelDefinitions() {
        multiplyBy2LookupTable = new byte[GaloisFieldSize];
        multiplyBy3LookupTable = new byte[GaloisFieldSize];
        initMultiplyLookupTables();

        multiplicativeInverse = new byte[GaloisFieldSize];
        initMultiplicativeInverse();

        substitutionBox = new byte[GaloisFieldSize];
        initSubstitutionBox();
    }

    byte getSubstitutedByte(byte value) {
        return substitutionBox[value & 0xff];
    }

    byte[] getMixRow(int rowPosition) {
        return mixRows[rowPosition];
    }

    byte lookupMultiplication(byte value, byte multiplier) {
        switch (multiplier) {
            case 1:
                return value;
            case 2:
                return multiplyBy2LookupTable[value & 0xFF];
            case 3:
                return multiplyBy3LookupTable[value & 0xFF];
            default:
                throw new UnsupportedOperationException("You can't lookup multiplication of value " + multiplier);
        }
    }

    private byte getInverse(byte value) {
        return multiplicativeInverse[value & 0xff];
    }

    private void initMultiplyLookupTables() {
        for (int index = 0; index < multiplyBy2LookupTable.length; index++) {
            multiplyBy2LookupTable[index] = multiplicationGalois256((byte) 2, (byte) index);
        }

        for (int index = 0; index < multiplyBy3LookupTable.length; index++) {
            multiplyBy3LookupTable[index] = multiplicationGalois256((byte) 3, (byte) index);
        }
    }

    private byte multiplicationGalois256(byte firstByte, byte secondByte) {
        byte result = 0;

        for (int counter = 0; counter < 8; counter++) {
            if ((secondByte & 1) != 0) {
                result ^= firstByte; // add
            }
            secondByte >>= 1; // divide by x

            boolean isCarrySet = (firstByte & 0x80) != 0;
            firstByte <<= 1; // multiply by x
            if (isCarrySet) {
                firstByte ^= ReducingPolynomial;
            }

            if (firstByte == 0 || secondByte == 0) {
                break;
            }
        }

        return result;
    }


    private void initMultiplicativeInverse() {
        for (int outerIndex = 0; outerIndex < GaloisFieldSize; outerIndex++) {
            for (int innerIndex = 0; innerIndex < GaloisFieldSize; innerIndex++) {
                byte result = multiplicationGalois256((byte) innerIndex, (byte) outerIndex);

                if (result == 1) {
                    multiplicativeInverse[outerIndex] = (byte) innerIndex;
                    break;
                }
            }
        }
    }

    private void initSubstitutionBox() {
        for (int index = 0; index < substitutionBox.length; index++) {
            byte value = (byte) index;

            value = getInverse(value);
            value = (byte) (value ^ circularLeftShift(value, 1) ^ circularLeftShift(value, 2) ^
                            circularLeftShift(value, 3) ^ circularLeftShift(value, 4));
            value ^= 0x63;

            substitutionBox[index] = value;
        }
    }

    private byte circularLeftShift(byte value, int shiftCount) {
        return (byte) (((value & 0xff) << shiftCount) | ((value & 0xff) >>> (Byte.SIZE - shiftCount)));
    }

    private byte circularRightShift(byte value, int shiftCount) {
        return (byte) (((value & 0xff) << shiftCount) | ((value & 0xff) >>> (Byte.SIZE - shiftCount)));
    }
}
