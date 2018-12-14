package dsa;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class DsaPublicKey {
    private BigInteger primeNumber;
    private BigInteger primeDivisor;
    private BigInteger generator;
    private BigInteger publicKeyPart;
}
