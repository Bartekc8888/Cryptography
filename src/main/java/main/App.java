package main;

import java.nio.charset.StandardCharsets;

import Algorithms.AlgorithmFactory;
import Algorithms.AlgorithmType;
import api.CryptographyAlgorithm;

public class App
{
    private static String message = "Message to encrypt";
    private static String password = "abcdefg_1234";

    public static void main( String[] args )
    {
        CryptographyAlgorithm algorithm = AlgorithmFactory.createAlgorithm(AlgorithmType.AES);
        byte[] encryptedMessage = algorithm.encrypt(password.getBytes(StandardCharsets.UTF_8), message.getBytes());

        System.out.println( "Hello World!" );
    }
}
