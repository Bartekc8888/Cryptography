package dsa;

import elgamal.Metadata;

import java.nio.ByteBuffer;

public class DsaMetadataConverter {
    public static final int numbersLength = 8 * Integer.BYTES;

    static byte[] createDsaMetadataBlock(DsaMetadata dsaMetaData) {
        ByteBuffer buffer = ByteBuffer.allocate(numbersLength);
        buffer.putInt(dsaMetaData.getMetadataVersion());
        buffer.putInt(dsaMetaData.getMetadataSize());
        buffer.putInt(dsaMetaData.getPrimeNumberLength());
        buffer.putInt(dsaMetaData.getPrimeDivisorLength());
        buffer.putInt(dsaMetaData.getGeneratorLength());
        buffer.putInt(dsaMetaData.getPublicKeyLength());
        buffer.putInt(dsaMetaData.getRLength());
        buffer.putInt(dsaMetaData.getSLength());


        return buffer.array();
    }

    static DsaMetadata retriveMetadataBlock(byte[] dsaMetadataBlock) {
        ByteBuffer buffer = ByteBuffer.wrap(dsaMetadataBlock, 0, numbersLength);

        int metadataVersion = buffer.getInt();
        int metadataSize = buffer.getInt();
        int primeNumberLength = buffer.getInt();
        int primeDivisorLength = buffer.getInt();
        int generatorLength = buffer.getInt();
        int publicKeyLength = buffer.getInt();
        int rLength = buffer.getInt();
        int sLength = buffer.getInt();

        return new DsaMetadata(metadataVersion, metadataSize, primeNumberLength, primeDivisorLength, generatorLength, publicKeyLength,
                rLength, sLength);
    }
}
