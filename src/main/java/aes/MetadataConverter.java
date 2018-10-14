package aes;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

class MetadataConverter {

    private static int ACRONYM_LENGTH = 4;

    static byte[] createMetadataBlock(Metadata metaData) {
        byte[] metaDataBlock = new byte[Metadata.METADATA_SIZE_IN_BYTES];

        String acronym = metaData.getAlogrithmVersion().getAcronym();
        byte[] acronymBytes = acronym.getBytes(StandardCharsets.UTF_8);

        int numbersLength = Integer.BYTES + Long.BYTES;
        ByteBuffer buffer = ByteBuffer.allocate(numbersLength);
        buffer.putInt(metaData.getMetadataVersion());
        buffer.putLong(metaData.getFileLength());
        byte[] fileLengthBytes = buffer.array();

        System.arraycopy(fileLengthBytes, 0, metaDataBlock, 0, numbersLength);
        System.arraycopy(acronymBytes, 0, metaDataBlock, numbersLength, ACRONYM_LENGTH);

        return metaDataBlock;
    }

    static Metadata retriveMetadataBlock(byte[] metaDataBlock) {
        int numbersLength = Integer.BYTES + Long.BYTES;
        ByteBuffer buffer = ByteBuffer.wrap(metaDataBlock, 0, numbersLength);

        int metadataVersion = buffer.getInt();
        long fileLength = buffer.getLong();
        String acronym = new String(metaDataBlock, numbersLength, ACRONYM_LENGTH);
        AESVersion aesVersion = AESVersion.fromAcronym(acronym);

        return new Metadata(metadataVersion, fileLength, aesVersion);
    }
}
