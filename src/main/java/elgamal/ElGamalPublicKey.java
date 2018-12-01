package elgamal;

import largeinteger.LargeInteger;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ElGamalPublicKey {
    private LargeInteger primeNumber;
    private LargeInteger generator;
    private LargeInteger publicKeyPart;
}
