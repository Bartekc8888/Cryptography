package dsa;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DsaMetadata {
    public static int CURRENT_METADATA_VERSION = 1;

    private int metadataVersion;
    private int metadataSize;

    private int primeNumberLength;
    private int primeDivisorLength;
    private int generatorLength;
    private int publicKeyLength;
}
