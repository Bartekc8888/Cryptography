package dsa;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
public class Signature {
    BigInteger r;
    BigInteger s;
}
