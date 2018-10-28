package api;

import java.io.File;

public interface CryptographyAlgorithm {

    void encrypt(byte[] key, File inputFile, File outputFile);

    byte[] encrypt(byte[] key, String data);

    byte[] encrypt(byte[] key, byte[] data);

    void decrypt(byte[] key, File inputFile, File outputFile);

    byte[] decrypt(byte[] key, byte[] data);
}
