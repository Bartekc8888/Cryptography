package elgamal;

import largeinteger.LargeInteger;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EncryptedBlock {
    LargeInteger cipherRandom;
    LargeInteger cipherEncrypted;
}
