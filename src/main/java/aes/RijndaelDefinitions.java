package aes;

class RijndaelDefinitions {
    private static final int GALOIS_FIELD_SIZE = 256;
    private static final int ROUND_COUNT = GALOIS_FIELD_SIZE;
    private static final int UNSIGNED_BYTE_CONST = 0xff;
    private static final int REDUCING_POLYNOMIAL = 0b1_0001_1011; // x^8 + x^4 + x^3 + x + 1

    private final byte[] roundConstantLookupTable;
    private final byte[] substitutionBox;

    private final byte[] supportedMultiplyLookup = {1, 2, 3, 9, 11, 13, 14};
    private final byte[][] multiplyLookupTable;
    private final byte[] multiplicativeInverse;
    private final byte[][] mixRows = {{2, 3, 1, 1},
                                      {1, 2, 3, 1},
                                      {1, 1, 2, 3},
                                      {3, 1, 1, 2}};
    private final byte[][] inverseMixRows = {{14, 11, 13, 9},
                                             {9, 14, 11, 13},
                                             {13, 9, 14, 11},
                                             {11, 13, 9, 14}};


    private final byte[] inverseSubstitutionBox;

    RijndaelDefinitions() {
        multiplyLookupTable = new byte[supportedMultiplyLookup.length][];
        initMultiplyLookupTables();

        multiplicativeInverse = new byte[GALOIS_FIELD_SIZE];
        initMultiplicativeInverse();

        substitutionBox = new byte[GALOIS_FIELD_SIZE];
        initSubstitutionBox();

        roundConstantLookupTable = new byte[ROUND_COUNT];
        initRoundConstants();

        inverseSubstitutionBox = new byte[GALOIS_FIELD_SIZE];
        initInverseSubstitutionBox();
    }

    byte getSubstitutedByte(byte value) {
        return substitutionBox[value & UNSIGNED_BYTE_CONST];
    }

    byte getInverseSubstitutedByte(byte value) {
        return inverseSubstitutionBox[value & UNSIGNED_BYTE_CONST];
    }

    byte getRoundConstant(byte round) {
        return roundConstantLookupTable[round];
    }

    byte[] getMixRow(int rowPosition) {
        return mixRows[rowPosition];
    }

    byte[] getInverseMixRow(int rowPosition) {
        return inverseMixRows[rowPosition];
    }

    byte lookupMultiplication(byte value, byte multiplier) {
        switch (multiplier) {
            case 1:
                return value;
            case 2:
                return multiplyLookupTable[1][value & UNSIGNED_BYTE_CONST];
            case 3:
                return multiplyLookupTable[2][value & UNSIGNED_BYTE_CONST];
            case 9:
                return multiplyLookupTable[3][value & UNSIGNED_BYTE_CONST];
            case 11:
                return multiplyLookupTable[4][value & UNSIGNED_BYTE_CONST];
            case 13:
                return multiplyLookupTable[5][value & UNSIGNED_BYTE_CONST];
            case 14:
                return multiplyLookupTable[6][value & UNSIGNED_BYTE_CONST];
            default:
                throw new UnsupportedOperationException("You can't lookup multiplication of value " + multiplier);
        }
    }

    private byte getInverse(byte value) {
        return multiplicativeInverse[value & UNSIGNED_BYTE_CONST];
    }

    private void initMultiplyLookupTables() {
        for (int lookupIndex = 0; lookupIndex < supportedMultiplyLookup.length; lookupIndex++) {
            byte[] lookupTable = new byte[GALOIS_FIELD_SIZE];

            for (int index = 0; index < GALOIS_FIELD_SIZE; index++) {
                lookupTable[index] = multiplicationGalois256(supportedMultiplyLookup[lookupIndex], (byte) index);
            }

            multiplyLookupTable[lookupIndex] = lookupTable;
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
                firstByte ^= REDUCING_POLYNOMIAL;
            }

            if (firstByte == 0 || secondByte == 0) {
                break;
            }
        }

        return result;
    }


    private void initMultiplicativeInverse() {
        for (int outerIndex = 0; outerIndex < GALOIS_FIELD_SIZE; outerIndex++) {
            for (int innerIndex = 0; innerIndex < GALOIS_FIELD_SIZE; innerIndex++) {
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

    private void initRoundConstants() {
        roundConstantLookupTable[0] = 1;
        byte previousRC = roundConstantLookupTable[0];

        for (int i = 1; i < ROUND_COUNT; i++) {
            roundConstantLookupTable[i] = lookupMultiplication(previousRC, (byte) 2);

            previousRC = roundConstantLookupTable[i];
        }

    }

    private byte circularLeftShift(byte value, int shiftCount) {
        return (byte) (((value & UNSIGNED_BYTE_CONST) << shiftCount) | ((value & UNSIGNED_BYTE_CONST) >>> (Byte.SIZE - shiftCount)));
    }

    private void initInverseSubstitutionBox() {
        if (substitutionBox == null) {
            throw new IllegalStateException("Inverse substitution box should be initialized after substitution box");
        }

        for (int i = 0; i < GALOIS_FIELD_SIZE; i++) {
            byte sBoxValue = this.substitutionBox[i];
            inverseSubstitutionBox[sBoxValue & UNSIGNED_BYTE_CONST] = (byte) i;
        }
    }
}
