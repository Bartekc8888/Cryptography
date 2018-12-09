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
    private int generatorLength;
    private int primeDivisorLength;
    private int publicKeyLength;
    private int rLength;
    private int sLength;
}
