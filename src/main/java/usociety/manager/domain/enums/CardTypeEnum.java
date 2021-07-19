package usociety.manager.domain.enums;

import java.util.Arrays;

public enum CardTypeEnum {

    CREDIT("CREDIT", 0),
    DEBIT("DEBIT", 1);

    private String value;
    private int code;

    CardTypeEnum(String value, int code) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public int getCode() {
        return code;
    }

    public static CardTypeEnum fromCode(int code) {
        return Arrays.stream(CardTypeEnum.values())
                .filter(type -> type.code == code)
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported card type code."));
    }
}
