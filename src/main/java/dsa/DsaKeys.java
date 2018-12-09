package dsa;

import largeinteger.LargeInteger;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DsaKeys {
    private LargeInteger privateKey;
    private DsaPublicKey publicKey;
}
