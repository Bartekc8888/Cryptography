package elgamal;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import largeinteger.LargeInteger;
import org.apache.commons.lang3.ArrayUtils;

public class KeyConverter {
    private static final int keyLengthsInBytes = 48;

    public static byte[] convertToData(ElGamalPublicKey publicKey) {
        byte[] prime = convertTo(publicKey.getPrimeNumber());
        byte[] generator = convertTo(publicKey.getGenerator());
        byte[] pubKeyPart = convertTo(publicKey.getPublicKeyPart());

        return ArrayUtils.addAll(ArrayUtils.addAll(prime, generator), pubKeyPart);
    }

    public static byte[] convertToData(LargeInteger privateKey) {
        return convertTo(privateKey);
    }

    public static ElGamalPublicKey convertFromData(byte[] publicKeyData) {
        int oneThirdOfLength = publicKeyData.length / 3;
        byte[] primeBytes = Arrays.copyOfRange(publicKeyData, 0, oneThirdOfLength);
        byte[] generatorBytes = Arrays.copyOfRange(publicKeyData, oneThirdOfLength, 2*oneThirdOfLength);
        byte[] pubKeyPartBytes = Arrays.copyOfRange(publicKeyData, 2*oneThirdOfLength, 3*oneThirdOfLength);

        return new ElGamalPublicKey(convertFrom(primeBytes), convertFrom(generatorBytes), convertFrom(pubKeyPartBytes));
    }

    public static LargeInteger convertPrivateFromData(byte[] privateKey) {
        return convertFrom(privateKey);
    }

    private static byte[] convertTo(LargeInteger number) {
        String paddedString = paddWithZeros(number.toString(), keyLengthsInBytes);

        return Base64.getEncoder().encode(paddedString.getBytes(StandardCharsets.UTF_8));
    }

    private static LargeInteger convertFrom(byte[] number) {
        byte[] decoded = Base64.getDecoder().decode(number);

        return LargeInteger.of(new String(decoded, StandardCharsets.UTF_8));
    }

    private static String paddWithZeros(String unpadded, int paddingSize) { // up to 32 chars
        StringBuilder sb = new StringBuilder(32);

        for (int toPrepend = paddingSize - unpadded.length(); toPrepend > 0; toPrepend--) {
            sb.append('0');
        }
        sb.append(unpadded);

        return sb.toString();
    }
}
