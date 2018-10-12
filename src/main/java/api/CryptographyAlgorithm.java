package api;

public interface CryptographyAlgorithm {
    byte[] encrypt(byte[] key, byte[] data);
    byte[] decrypt(byte[] key, byte[] data);
}
