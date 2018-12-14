package dsa;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
public class DsaKeys {
    private BigInteger privateKey;
    private DsaPublicKey publicKey;
}
