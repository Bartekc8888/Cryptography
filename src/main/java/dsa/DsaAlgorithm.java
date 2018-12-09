package dsa;

import elgamal.*;
import largeinteger.LargeInteger;
import org.apache.commons.io.IOUtils;
import java.security.MessageDigest;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DsaAlgorithm {

    public DsaAlgorithm() {

    }

    private Signature sign(DsaKeys userKeys, LargeInteger hashDataBlock) {
        LargeInteger randomNumber = LargeInteger.createRandom(LargeInteger.ONE, userKeys.getPublicKey().getPrimeDivisor());

        LargeInteger r = userKeys.getPublicKey().getGenerator().modularPower(randomNumber, userKeys.getPublicKey().getPrimeNumber());
        r.modulo(userKeys.getPublicKey().getPrimeDivisor());

        LargeInteger randomNumberInverse = randomNumber.multiplicativeInverse(userKeys.getPublicKey().getPrimeNumber());
        LargeInteger xr = userKeys.getPrivateKey().multiply(r);
        LargeInteger firstPart = randomNumberInverse.multiply(hashDataBlock.add(xr));
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
                bufferedOutputStream.write(primeNumberBytes);
                bufferedOutputStream.write(generatorBytes);
                bufferedOutputStream.write(primeDivisorBytes);
                bufferedOutputStream.write(pubKeyPartBytes);
                bufferedOutputStream.write(rBytes);
                bufferedOutputStream.write(sBytes);
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
                    primeNumberBytes.length, generatorBytes.length, primeDivisorBytes.length, pubKeyPartBytes.length, rSize, sSize);
            byte[] metadataBlock = DsaMetadataConverter.createDsaMetadataBlock(metadata);

            randomAccess.seek(0);
            randomAccess.write(metadataBlock);
        } catch (IOException e) {
            throw new RuntimeException("Signing failed", e);
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
}
