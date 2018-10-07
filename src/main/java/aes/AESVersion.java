package aes;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AESVersion {
    AES_128(4, 11),
    AES_192(6, 13),
    AES_256(8, 15);

    private final int wordsCount;
    private final int roundsCount;
}
