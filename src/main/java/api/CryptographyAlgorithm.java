package api;

public interface CryptographyAlgorithm {
    byte[] encode(byte[] key, byte[] data);
    byte[] decode(byte[] key, byte[] data);
}
