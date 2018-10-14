package aes;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AESVersion {
    AES_128(4, 11, 16, "A128"),
    AES_192(6, 13, 24, "A192"),
    AES_256(8, 15, 32, "A256");

    private final int wordsCount;
    private final int roundsCount;
    private final int keySizeInBytes;
    private final String acronym;

    public String getAcronym() {
        return acronym;
    }

    public static AESVersion fromAcronym(String acronym){
        for (AESVersion aesEnum : values()) {
            if (aesEnum.getAcronym().equalsIgnoreCase(acronym))
                return aesEnum;
        }
        throw new IllegalArgumentException("Acronym " + acronym + " not defined.");
    }
}
