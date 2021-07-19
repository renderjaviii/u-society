package usociety.manager.domain.enums;

import java.util.Arrays;

public enum DocumentTypeEnum {

    CC("CC", 0),
    NIT("NIT", 1);

    private String value;
    private int code;

    DocumentTypeEnum(String value, int code) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public int getCode() {
        return code;
    }

    public static DocumentTypeEnum fromCode(int code) {
        return Arrays.stream(DocumentTypeEnum.values())
                .filter(type -> type.code == code)
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported card type code."));
    }
}
