package largeinteger;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DivisionResult {
    private final LargeInteger result;
    private final LargeInteger reminder;
}
