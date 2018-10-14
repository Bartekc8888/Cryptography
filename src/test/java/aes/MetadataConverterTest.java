package aes;

import org.junit.Assert;
import org.junit.Test;

public class MetadataConverterTest {

    @Test
    public void createMetadataBlock() {
        Metadata metaData = new Metadata(1, 400, AESVersion.AES_256);

        byte[] metadataBlock = MetadataConverter.createMetadataBlock(metaData);

        Assert.assertEquals(16, metadataBlock.length);
        Assert.assertEquals(metaData.getMetadataVersion(), metadataBlock[3]);
    }

    @Test
    public void retriveMetadataBlock() {
        byte[] testDataBlock = {0, 0, 0, 1,
                                0, 0, 0, 0,
                                0, 0, 1, -112,
                                65, 50, 53, 54};

        Metadata expectedMetadata = new Metadata(1, 400, AESVersion.AES_256);

        Metadata retrivedMetadata = MetadataConverter.retriveMetadataBlock(testDataBlock);

        Assert.assertEquals(expectedMetadata, retrivedMetadata);
    }
}