package elgamal;

import java.io.File;

import api.CryptographyAlgorithm;
import largeinteger.LargeInteger;

public class ElGamalAlgorithm implements CryptographyAlgorithm {

    public ElGamalAlgorithm() {

    }

    @Override
    public void encrypt(String password, File inputFile, File outputFile) {
    }

    @Override
    public byte[] encrypt(String password, String data) {
        return new byte[0];
    }

    @Override
    public byte[] encrypt(String password, byte[] data) {
        return new byte[0];
    }

    @Override
    public void decrypt(String password, File inputFile, File outputFile) {

    }

    @Override
    public byte[] decrypt(String password, byte[] data) {
        return new byte[0];
    }

    public EncryptedBlock encrypt(ElGamalPublicKey publicKey, LargeInteger dataBlock) {
        LargeInteger randomNumber = LargeInteger.createRandom(8);

        LargeInteger c1 = publicKey.getGenerator().modularPower(randomNumber, publicKey.getPrimeNumber());

        LargeInteger dataModulo = dataBlock.modulo(publicKey.getPrimeNumber());
        LargeInteger publicPartToRandomPower = publicKey.getPublicKeyPart().modularPower(randomNumber, publicKey.getPrimeNumber());
        LargeInteger c2 = dataModulo.multiply(publicPartToRandomPower)
                                    .modulo(publicKey.getPrimeNumber());

        return new EncryptedBlock(c1, c2);
    }

    public LargeInteger decrypt(ElGamalKeys elGamalKeys, EncryptedBlock encryptedBlock) {
        LargeInteger primeNumber = elGamalKeys.getPublicKey().getPrimeNumber();

        LargeInteger randomModular = encryptedBlock.getCipherRandom().modularPower(elGamalKeys.getPrivateKey(), primeNumber);
        LargeInteger multiplicativeInverse = randomModular.modularPower(primeNumber.subtract(LargeInteger.TWO),
                                                                        primeNumber);

        return multiplicativeInverse.multiply(encryptedBlock.getCipherEncrypted()).modulo(primeNumber);
    }
}
