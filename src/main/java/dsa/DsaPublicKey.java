package dsa;

import largeinteger.LargeInteger;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class DsaPublicKey {
    private LargeInteger primeNumber;
    private LargeInteger primeDivisor;
    private LargeInteger generator;
    private LargeInteger publicKeyPart;
}
