package dsa;

import api.CryptographyAlgorithm;
import elgamal.*;
import largeinteger.LargeInteger;
import java.security.MessageDigest;

import java.io.*;

public class DsaAlgorithm implements CryptographyAlgorithm {

    public DsaAlgorithm() {

    }

    private Signature sign(DsaKeys userKeys, LargeInteger hashDataBlock) {
        LargeInteger k = LargeInteger.createRandom(LargeInteger.ONE, userKeys.getPublicKey().getPrimeDivisor());

        LargeInteger r = userKeys.getPublicKey().getGenerator().modularPower(k, userKeys.getPublicKey().getPrimeNumber());
        r = r.modulo(userKeys.getPublicKey().getPrimeDivisor());

        LargeInteger randomNumberInverse = k.multiplicativeInverse(userKeys.getPublicKey().getPrimeDivisor());
        LargeInteger xr = userKeys.getPrivateKey().multiply(r);
        LargeInteger firstPart = randomNumberInverse.multiply(hashDataBlock.add(xr).modulo(userKeys.getPublicKey().getPrimeDivisor()));
        LargeInteger s = firstPart.modulo(userKeys.getPublicKey().getPrimeDivisor());


        return new Signature(r, s);
    }

    public void sign(DsaKeys keys, File inputFile, File outputFile) {
        DsaMetadata emptyMetadata = new DsaMetadata(Metadata.CURRENT_METADATA_VERSION, 0, 0, 0, 0, 0, 0, 0);
        byte[] emptyMetadataBlock = DsaMetadataConverter.createDsaMetadataBlock(emptyMetadata);
        byte[] publicKeyBytes = DsaKeyConverter.convertToData(keys.getPublicKey());
        byte[] primeNumberBytes = DsaKeyConverter.convertPrimeNumberToData(keys.getPublicKey());
        byte[] generatorBytes = DsaKeyConverter.convertGeneratorToData(keys.getPublicKey());
        byte[] primeDivisorBytes = DsaKeyConverter.convertPrimeDivisorToData(keys.getPublicKey());
        byte[] pubKeyPartBytes = DsaKeyConverter.convertPubKeyPartToData(keys.getPublicKey());
        int rSize=-1, sSize=-1;

        try {
            try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 65536)) {

                bufferedOutputStream.write(emptyMetadataBlock);
                bufferedOutputStream.write(publicKeyBytes);

                byte[] digestMessage = createSha1(inputFile);
                LargeInteger hashDataBlock = LargeInteger.of(digestMessage);
                Signature signature = sign(keys, hashDataBlock);
                byte[] rBytes = DsaKeyConverter.convertToData(signature.getR());
                byte[] sBytes = DsaKeyConverter.convertToData(signature.getS());
                rSize = rBytes.length;
                sSize = sBytes.length;

                bufferedOutputStream.write(rBytes);
                System.out.println("r " + rBytes.length);
                bufferedOutputStream.write(sBytes);
                System.out.println("s " + sBytes.length);
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

        try (RandomAccessFile randomAccess = new RandomAccessFile(outputFile, "rw")) {
            DsaMetadata metadata = new DsaMetadata(DsaMetadata.CURRENT_METADATA_VERSION, DsaMetadataConverter.numbersLength,
                    primeNumberBytes.length, primeDivisorBytes.length, generatorBytes.length, pubKeyPartBytes.length, rSize, sSize);
            byte[] metadataBlock = DsaMetadataConverter.createDsaMetadataBlock(metadata);

            randomAccess.seek(0);
            randomAccess.write(metadataBlock);
        } catch (IOException e) {
            throw new RuntimeException("Signing failed", e);
        }
    }

    private LargeInteger veryfy(DsaPublicKey dsaPublicKey, Signature signature, LargeInteger hashDataBlock) {
        LargeInteger w = signature.getS().multiplicativeInverse(dsaPublicKey.getPrimeDivisor());
        LargeInteger u1 = hashDataBlock.multiply(w).modulo(dsaPublicKey.getPrimeDivisor());
        LargeInteger u2 = signature.getR().multiply(w).modulo(dsaPublicKey.getPrimeDivisor());
        LargeInteger v1 = dsaPublicKey.getGenerator().modularPower(u1, dsaPublicKey.getPrimeNumber());
        LargeInteger v2 = dsaPublicKey.getPublicKeyPart().modularPower(u2, dsaPublicKey.getPrimeNumber());
        LargeInteger v = v1.multiply(v2).modulo(dsaPublicKey.getPrimeNumber());
        v = v.modulo(dsaPublicKey.getPrimeDivisor());

        return v;
    }

    public boolean veryfy(File inputFile, File signatureFile) {
        try {
            try (FileInputStream fileInputStream = new FileInputStream(signatureFile);
                 BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, 65536)) {

                byte[] metadataBytes = new byte[DsaMetadataConverter.numbersLength];
                int metadataBytesRead = bufferedInputStream.read(metadataBytes);
                if (metadataBytesRead != DsaMetadataConverter.numbersLength) {
                    throw new RuntimeException("Could not read metadata.");
                }

                DsaMetadata metadata = DsaMetadataConverter.retriveMetadataBlock(metadataBytes);
                byte[] primeNumberBytes = new byte[metadata.getPrimeNumberLength()];
                int primeNumberBytesRead = bufferedInputStream.read(primeNumberBytes);

                byte[] generatorBytes = new byte[metadata.getGeneratorLength()];
                int generatorBytesRead = bufferedInputStream.read(generatorBytes);

                byte[] primeDivisorBytes = new byte[metadata.getPrimeDivisorLength()];
                int primeDivisorBytesRead = bufferedInputStream.read(primeDivisorBytes);

                byte[] publicKeyBytes = new byte[metadata.getPublicKeyLength()];
                int publicKeyBytesRead = bufferedInputStream.read(publicKeyBytes);

                byte[] rBytes = new byte[metadata.getRLength()];
                int rBytesRead = bufferedInputStream.read(rBytes);

                byte[] sBytes = new byte[metadata.getSLength()];
                int sBytesRead = bufferedInputStream.read(sBytes);

                DsaPublicKey dsaPublicKey = DsaKeyConverter.convertFromData(primeNumberBytes, primeDivisorBytes, generatorBytes, publicKeyBytes);
                Signature signature = new Signature(DsaKeyConverter.convertPrivateFromData(rBytes), DsaKeyConverter.convertPrivateFromData(sBytes));
                byte[] digestMessage = createSha1(inputFile);
                LargeInteger hashDataBlock = LargeInteger.of(digestMessage);

                LargeInteger v = veryfy(dsaPublicKey, signature, hashDataBlock);
                return v.toString().equals(signature.getR().toString());

            } catch (IOException e) {
                throw new RuntimeException("Veryfing failed", e);
            }
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
