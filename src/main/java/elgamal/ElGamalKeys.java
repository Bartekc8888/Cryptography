package elgamal;

import largeinteger.LargeInteger;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ElGamalKeys {
    private LargeInteger privateKey;
    private ElGamalPublicKey publicKey;
}
