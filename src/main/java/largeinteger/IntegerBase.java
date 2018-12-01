package largeinteger;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IntegerBase {
    BASE_10(10), BASE_256(256);

    private int base;
}
