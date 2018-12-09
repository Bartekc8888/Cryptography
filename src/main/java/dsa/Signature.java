package dsa;

import largeinteger.LargeInteger;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Signature {
    LargeInteger r;
    LargeInteger s;
}
