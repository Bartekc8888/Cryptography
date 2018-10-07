package main;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import aes.AESAlgorithm;
import aes.AESVersion;
import api.CryptographyAlgorithm;

public class App
{
    private static String message = "Message to encrypt";
    private static String password = "abcdefg_1234";

    public static void main( String[] args )
    {
        CryptographyAlgorithm algorithm = new AESAlgorithm(AESVersion.AES_128);
        byte[] encodedMessage = algorithm.encode(password.getBytes(StandardCharsets.UTF_8), message.getBytes());

        System.out.println( "Hello World!" );
    }
}
