package dsa;

import java.nio.ByteBuffer;

public class SignatureMetadataConverter {
    public static final int numbersLength = 4 * Integer.BYTES;

    static byte[] createSignatureMetadataBlock(SignatureMetadata signatureMetaData) {
        ByteBuffer buffer = ByteBuffer.allocate(numbersLength);
        buffer.putInt(signatureMetaData.getMetadataVersion());
        buffer.putInt(signatureMetaData.getMetadataSize());
        buffer.putInt(signatureMetaData.getRLength());
        buffer.putInt(signatureMetaData.getSLength());


        return buffer.array();
    }

    static SignatureMetadata retriveSignatureMetadataBlock(byte[] signatureMetadataBlock) {
        ByteBuffer buffer = ByteBuffer.wrap(signatureMetadataBlock, 0, numbersLength);

        int metadataVersion = buffer.getInt();
        int metadataSize = buffer.getInt();
        int rLength = buffer.getInt();
        int sLength = buffer.getInt();

        return new SignatureMetadata(metadataVersion, metadataSize, rLength, sLength);
    }
}