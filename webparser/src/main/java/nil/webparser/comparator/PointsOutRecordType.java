package nil.webparser.comparator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PointsOutRecordType {

    /**
     * Header (total points, base points, bonus points)
     */
    HEADER("H"),

    /**
     * Transaction, bonus details
     */
    TRANSACTION("T");

    @Getter
    private final String value;
}
