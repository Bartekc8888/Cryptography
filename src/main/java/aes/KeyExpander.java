package aes;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class KeyExpander {

    private final RijndaelDefinitions rijndaelDefinitions = new RijndaelDefinitions();
    private AESVersion aesVersion;

    private static final int BYTES_PER_WORD = 4;

    List<byte[]>  expandKey(byte[] originalKey) {
        byte[] allKeys = new byte[aesVersion.getWordsCount() * BYTES_PER_WORD * aesVersion.getRoundsCount()];
        System.arraycopy(originalKey, 0, allKeys, 0, originalKey.length);

        int wordsGenerated = originalKey.length / BYTES_PER_WORD;
        while (wordsGenerated < aesVersion.getWordsCount() * aesVersion.getRoundsCount()) {
            byte[] lastWord = getLastXWord(allKeys, wordsGenerated * BYTES_PER_WORD, 1);
            byte[] lastXWord = getLastXWord(allKeys, wordsGenerated * BYTES_PER_WORD, aesVersion.getWordsCount());

            if (wordsGenerated % aesVersion.getWordsCount() == 0) {
                keyExpansionCore(lastWord, (byte) (wordsGenerated / aesVersion.getWordsCount()));
            } else {
                if (aesVersion.getWordsCount() > 6 && wordsGenerated % aesVersion.getWordsCount() == 4) {
                    substituteBytes(lastWord);
                }
            }

            byte[] word = XORWords(lastXWord, lastWord);
            System.arraycopy(word, 0, allKeys, wordsGenerated * BYTES_PER_WORD, word.length);
            wordsGenerated++;
        }

        return splitKeys(allKeys);
    }

    private void keyExpansionCore(byte[] keyRow, byte iterationNumber) {
        rotateArrayLeft(keyRow);
        substituteBytes(keyRow);
        addRoundConstant(keyRow, iterationNumber);
    }

    private void rotateArrayLeft(byte[] array) {
        byte tmpByte = array[0];
        array[0] = array[1];
        array[1] = array[2];
        array[2] = array[3];
        array[3] = tmpByte;
    }

    private void substituteBytes(byte[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = rijndaelDefinitions.getSubstitutedByte(array[i]);
        }
    }

    private void addRoundConstant(byte[] keyRow, byte iterationNumber) {
        keyRow[0] ^= rijndaelDefinitions.getRoundConstant((byte) (iterationNumber - 1));
    }

    private byte[] getLastXWord(byte[] blockOfWords, int currentPosition, int positionFromEnd) {
        if (currentPosition < BYTES_PER_WORD * positionFromEnd) {
            throw new UnsupportedOperationException("Array must be at least " + BYTES_PER_WORD * positionFromEnd + " bytes long");
        }

        byte[] word = new byte[BYTES_PER_WORD];
        for (int i = 0; i < BYTES_PER_WORD; i++) {
            word[i] = blockOfWords[currentPosition - (positionFromEnd * BYTES_PER_WORD - i)];
        }

        return word;
    }

    private byte[] XORWords(byte[] firstWord, byte[] secondWord) {
        if (firstWord.length != secondWord.length) {
            throw new UnsupportedOperationException("Array lengths must be equal");
        }

        byte[] value = new byte[firstWord.length];
        for (int i = 0; i < firstWord.length; i++) {
            value[i] = (byte) (firstWord[i] ^ secondWord[i]);
        }

        return  value;
    }

    private List<byte[]> splitKeys(byte[] allKeys) {
        List<byte[]> expandedKeys = new ArrayList<>();

        for (int byteIndex = 0; byteIndex < allKeys.length; byteIndex+= aesVersion.getWordsCount() * BYTES_PER_WORD) {
            byte[] key = new byte[aesVersion.getWordsCount() * BYTES_PER_WORD];
            System.arraycopy(allKeys, byteIndex, key, 0, key.length);

            expandedKeys.add(key);
        }

        return expandedKeys;
    }
}
