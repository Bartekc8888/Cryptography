package elgamal;

import java.nio.ByteBuffer;

class MetadataConverter {
    public static final int numbersLength = 3 * Integer.BYTES + Long.BYTES;

    static byte[] createMetadataBlock(Metadata metaData) {
        ByteBuffer buffer = ByteBuffer.allocate(numbersLength);
        buffer.putInt(metaData.getMetadataVersion());
        buffer.putInt(metaData.getMetadataSize());
        buffer.putInt(metaData.getKeyLength());
        buffer.putLong(metaData.getFileLength());

        return buffer.array();
    }

    static Metadata retriveMetadataBlock(byte[] metaDataBlock) {
        ByteBuffer buffer = ByteBuffer.wrap(metaDataBlock, 0, numbersLength);

        int metadataVersion = buffer.getInt();
        int metadataSize = buffer.getInt();
        int keyLength = buffer.getInt();
        long fileLength = buffer.getLong();

        return new Metadata(metadataVersion, metadataSize, keyLength, fileLength);
    }
}
