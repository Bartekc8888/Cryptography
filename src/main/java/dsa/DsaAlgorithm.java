package dsa;

import api.CryptographyAlgorithm;
import elgamal.*;

import java.math.BigInteger;
import java.security.MessageDigest;

import java.io.*;
import java.util.Random;

public class DsaAlgorithm implements CryptographyAlgorithm {

    public DsaAlgorithm() {

    }

    private Signature sign(DsaKeys userKeys, BigInteger hashDataBlock) {
        Random random = new Random();
        BigInteger k;
        do {
            k = new BigInteger(userKeys.getPublicKey().getPrimeDivisor().bitLength(),random);
        } while (k.compareTo(BigInteger.ONE) <= 0);
        BigInteger r = userKeys.getPublicKey().getGenerator().modPow(k, userKeys.getPublicKey().getPrimeNumber());
        r = r.mod(userKeys.getPublicKey().getPrimeDivisor());
        BigInteger randomNumberInverse = k.modInverse(userKeys.getPublicKey().getPrimeDivisor());
        BigInteger xr = userKeys.getPrivateKey().multiply(r);
        BigInteger firstPart = randomNumberInverse.multiply(hashDataBlock.add(xr).mod(userKeys.getPublicKey().getPrimeDivisor()));
        BigInteger s = firstPart.mod(userKeys.getPublicKey().getPrimeDivisor());
        return new Signature(r, s);
    }

    public void sign(DsaKeys keys, File inputFile, File signatureFile, File publicComponentsFile) {

        byte[] publicKeyBytes = DsaKeyConverter.convertToData(keys.getPublicKey());
        byte[] primeNumberBytes = DsaKeyConverter.convertPrimeNumberToData(keys.getPublicKey());
        byte[] primeDivisorBytes = DsaKeyConverter.convertPrimeDivisorToData(keys.getPublicKey());
        byte[] generatorBytes = DsaKeyConverter.convertGeneratorToData(keys.getPublicKey());
        byte[] pubKeyPartBytes = DsaKeyConverter.convertPubKeyPartToData(keys.getPublicKey());
        System.out.println("Write primeNumber: " + primeNumberBytes.length);
        System.out.println("Write primeDivisor: " + primeDivisorBytes.length);
        System.out.println("Write generator: " + generatorBytes.length);
        System.out.println("Write pubKeyPart: " + pubKeyPartBytes.length);
        int rSize=-1, sSize=-1;

        try {
                byte[] digestMessage = createSha1(inputFile);
                BigInteger hashDataBlock = new BigInteger(digestMessage);
                Signature signature = sign(keys, hashDataBlock);
                byte[] rBytes = DsaKeyConverter.convertToData(signature.getR());
                byte[] sBytes = DsaKeyConverter.convertToData(signature.getS());

                DsaMetadata metadata = new DsaMetadata(DsaMetadata.CURRENT_METADATA_VERSION, DsaMetadataConverter.numbersLength,
                        primeNumberBytes.length, primeDivisorBytes.length, generatorBytes.length, pubKeyPartBytes.length);
                SignatureMetadata signatureMetadata = new SignatureMetadata(SignatureMetadata.CURRENT_METADATA_VERSION, SignatureMetadataConverter.numbersLength, rBytes.length, sBytes.length);

                byte[] metadataBlock = DsaMetadataConverter.createDsaMetadataBlock(metadata);
                byte[] signatureMetadataBlock = SignatureMetadataConverter.createSignatureMetadataBlock(signatureMetadata);
                System.out.println("Write first " + metadataBlock[0]);

            try (FileOutputStream fileOutputStream = new FileOutputStream(publicComponentsFile);
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 65536)) {
                    bufferedOutputStream.write(metadataBlock);
                    bufferedOutputStream.write(publicKeyBytes);
            }
            catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Signing failed");
            }

            try (FileOutputStream fileOutputStream = new FileOutputStream(signatureFile);
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 65536)) {
                bufferedOutputStream.write(signatureMetadataBlock);
                bufferedOutputStream.write(rBytes);
                System.out.println("Write r " + rBytes.length);
                bufferedOutputStream.write(sBytes);
                System.out.println("Write s " + sBytes.length);
            }
            catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Signing failed");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Signing failed");
        }

    }

    private BigInteger veryfy(DsaPublicKey dsaPublicKey, Signature signature, BigInteger hashDataBlock) {
        BigInteger w = signature.getS().modInverse(dsaPublicKey.getPrimeDivisor());
        BigInteger u1 = hashDataBlock.multiply(w).mod(dsaPublicKey.getPrimeDivisor());
        BigInteger u2 = signature.getR().multiply(w).mod(dsaPublicKey.getPrimeDivisor());
        BigInteger v1 = dsaPublicKey.getGenerator().modPow(u1, dsaPublicKey.getPrimeNumber());
        BigInteger v2 = dsaPublicKey.getPublicKeyPart().modPow(u2, dsaPublicKey.getPrimeNumber());
        BigInteger v = v1.multiply(v2).mod(dsaPublicKey.getPrimeNumber());
        v = v.mod(dsaPublicKey.getPrimeDivisor());

        return v;
    }

    public boolean veryfy(File inputFile, File signatureFile, File publicComponentsFile) {
        try {
            byte[] primeNumberBytes, primeDivisorBytes, generatorBytes, publicKeyBytes, rBytes, sBytes;
            try (FileInputStream fileInputStream = new FileInputStream(publicComponentsFile);
                 BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, 65536)) {

                byte[] metadataBytes = new byte[DsaMetadataConverter.numbersLength];
                int metadataBytesRead = bufferedInputStream.read(metadataBytes);
                if (metadataBytesRead != DsaMetadataConverter.numbersLength) {
                    throw new RuntimeException("Could not read metadata.");
                }

                DsaMetadata metadata = DsaMetadataConverter.retriveMetadataBlock(metadataBytes);
                System.out.println("Read primeNumber: " + metadata.getPrimeNumberLength());
                System.out.println("Read primeDivisor: " + metadata.getPrimeDivisorLength());
                System.out.println("Read generator: " + metadata.getGeneratorLength());
                System.out.println("Read publicKeyPart: " + metadata.getPublicKeyLength());

                primeNumberBytes = new byte[metadata.getPrimeNumberLength()];
                int primeNumberBytesRead = bufferedInputStream.read(primeNumberBytes);

                primeDivisorBytes = new byte[metadata.getPrimeDivisorLength()];
                int primeDivisorBytesRead = bufferedInputStream.read(primeDivisorBytes);

                generatorBytes = new byte[metadata.getGeneratorLength()];
                int generatorBytesRead = bufferedInputStream.read(generatorBytes);

                publicKeyBytes = new byte[metadata.getPublicKeyLength()];
                int publicKeyBytesRead = bufferedInputStream.read(publicKeyBytes);

            }catch (IOException e) {
                throw new RuntimeException("Veryfing failed", e);
            }

            try (FileInputStream fileInputStream = new FileInputStream(signatureFile);
                 BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, 65536)) {

                byte[] signatureMetadataBytes = new byte[SignatureMetadataConverter.numbersLength];
                int signatureMetadataBytesRead = bufferedInputStream.read(signatureMetadataBytes);
                if (signatureMetadataBytesRead != SignatureMetadataConverter.numbersLength) {
                    throw new RuntimeException("Could not read metadata.");
                }

                SignatureMetadata signatureMetadata = SignatureMetadataConverter.retriveSignatureMetadataBlock(signatureMetadataBytes);
                System.out.println("Read r: " + signatureMetadata.getRLength());
                System.out.println("Read s: " + signatureMetadata.getSLength());

                rBytes = new byte[signatureMetadata.getRLength()];
                int rBytesRead = bufferedInputStream.read(rBytes);

                sBytes = new byte[signatureMetadata.getSLength()];
                int sBytesRead = bufferedInputStream.read(sBytes);

            }catch (IOException e) {
                    throw new RuntimeException("Veryfing failed", e);
            }

                DsaPublicKey dsaPublicKey = DsaKeyConverter.convertFromData(primeNumberBytes, primeDivisorBytes, generatorBytes, publicKeyBytes);
                Signature signature = new Signature(DsaKeyConverter.convertPrivateFromData(rBytes), DsaKeyConverter.convertPrivateFromData(sBytes));
                byte[] digestMessage = createSha1(inputFile);
                BigInteger hashDataBlock = new BigInteger(digestMessage);

                BigInteger v = veryfy(dsaPublicKey, signature, hashDataBlock);
                return v.toString().equals(signature.getR().toString());

            }catch(Exception e)
            {
                e.printStackTrace();
                throw new RuntimeException("Veryfing failed");
            }

    }

    public byte[] createSha1(File file) throws Exception  {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        InputStream fis = new FileInputStream(file);
        int n = 0;
        byte[] buffer = new byte[8192];
        while (n != -1) {
            n = fis.read(buffer);
            if (n > 0) {
                digest.update(buffer, 0, n);
            }
        }
        return digest.digest();
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
}
