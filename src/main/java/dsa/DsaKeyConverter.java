package dsa;

import elgamal.ElGamalPublicKey;
import largeinteger.LargeInteger;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class DsaKeyConverter {

    public static byte[] convertToData(DsaPublicKey publicKey) {
        byte[] primeNumber = convertTo(publicKey.getPrimeNumber());
        byte[] generator = convertTo(publicKey.getGenerator());
        byte[] primeDivisor = convertTo(publicKey.getPrimeDivisor());
        byte[] pubKeyPart = convertTo(publicKey.getPublicKeyPart());

        return ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(primeNumber, generator), primeDivisor), pubKeyPart);
    }

    public static byte[] convertPrimeNumberToData(DsaPublicKey publicKey) {
        byte[] primeNumber = convertTo(publicKey.getPrimeNumber());

        return primeNumber;
    }

    public static byte[] convertGeneratorToData(DsaPublicKey publicKey) {
        byte[] generator = convertTo(publicKey.getGenerator());

        return generator;
    }

    public static byte[] convertPrimeDivisorToData(DsaPublicKey publicKey) {
        byte[] primeDivisor = convertTo(publicKey.getPrimeDivisor());

        return primeDivisor;
    }
    public static byte[] convertPubKeyPartToData(DsaPublicKey publicKey) {
        byte[] pubKeyPart = convertTo(publicKey.getPublicKeyPart());

        return pubKeyPart;
    }


    public static byte[] convertToData(LargeInteger privateKey) {
        return convertTo(privateKey);
    }

    public static DsaPublicKey convertFromData(byte[] primeBytes, byte[] primeDivisorBytes, byte[] generatorBytes, byte[] pubKeyPartBytes) {
        return new DsaPublicKey(convertFrom(primeBytes), convertFrom(primeDivisorBytes), convertFrom(generatorBytes), convertFrom(pubKeyPartBytes));
    }

    public static LargeInteger convertPrivateFromData(byte[] privateKey) {
        return convertFrom(privateKey);
    }

    private static byte[] convertTo(LargeInteger number) {
        String paddedString = paddWithZeros(number.toString(), number.toString().length());

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
