package api;

import java.io.File;

public interface CryptographyAlgorithm {

    void encrypt(String password, File inputFile, File outputFile);

    byte[] encrypt(String password, String data);

    byte[] encrypt(String password, byte[] data);

    void decrypt(String password, File inputFile, File outputFile);

    byte[] decrypt(String password, byte[] data);
}
