package elgamal;

import largeinteger.LargeInteger;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class ElGamalPublicKey {
    private LargeInteger primeNumber;
    private LargeInteger generator;
    private LargeInteger publicKeyPart;
}
