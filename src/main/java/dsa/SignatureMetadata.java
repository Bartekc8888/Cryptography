package dsa;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignatureMetadata {
    public static int CURRENT_METADATA_VERSION = 1;

    private int metadataVersion;
    private int metadataSize;

    private int rLength;
    private int sLength;
}