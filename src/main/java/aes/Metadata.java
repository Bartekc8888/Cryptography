package aes;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Metadata {
    public static int CURRENT_METADATA_VERSION = 1;
    public static int METADATA_SIZE_IN_BYTES = 16;

    private int metadataVersion;
    private long fileLength;
    private AESVersion alogrithmVersion;
}
