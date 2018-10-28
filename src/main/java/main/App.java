package main;

import java.io.File;
import java.io.IOException;

import Algorithms.AlgorithmFactory;
import Algorithms.AlgorithmType;
import aes.AESVersion;
import aes.KeyConverter;
import api.CryptographyAlgorithm;

public class App
{
    private static String message = "Message to encrypt";
    private static String password = "abcdefg_1234";

    public static void main(String[] args) throws IOException {
        File inputFile = new File("file.txt");
        if (!inputFile.exists()) {
            throw new IOException("File doesn't exist");
        }

        File encodedFile = new File("encodedFile.txt");
        File decodedFile = new File("decodedFile.txt");
        if (decodedFile.createNewFile() && encodedFile.createNewFile()) {
            throw new IOException("Cant create output files.");
        }

        CryptographyAlgorithm algorithm = AlgorithmFactory.createAlgorithm(AlgorithmType.AES);
        byte[] passwordBytes = KeyConverter.convertPasswordToBytes(password);
        byte[] key = KeyConverter.generateAESKeyFromPassword(passwordBytes, AESVersion.AES_128);

        algorithm.encrypt(key, inputFile, encodedFile);
        algorithm.decrypt(key, encodedFile, decodedFile);
    }
}
