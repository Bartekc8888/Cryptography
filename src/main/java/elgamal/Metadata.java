package elgamal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Metadata {
    public static int CURRENT_METADATA_VERSION = 1;

    private int metadataVersion;
    private int metadataSize;

    private int keyLength;
    private long fileLength;
}
