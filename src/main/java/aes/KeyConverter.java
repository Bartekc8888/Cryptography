package aes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class KeyConverter {

    static byte[] convertPasswordToBytes(String password) {
        return password.getBytes(StandardCharsets.UTF_8);
    }

    static byte[] generateAESKeyFromPassword(byte[] password, AESVersion version) {
        byte[] hash;
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            hash = sha.digest(password);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Algorithm not supported", ex);
        }

        byte[] generatedKey = new byte[version.getKeySizeInBytes()];
        System.arraycopy(hash, 0, generatedKey, 0, generatedKey.length);

        return generatedKey;
    }
}
