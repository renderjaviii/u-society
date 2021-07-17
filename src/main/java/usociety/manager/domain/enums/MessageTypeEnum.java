package usociety.manager.domain.enums;

import java.util.Arrays;

public enum MessageTypeEnum {

    TEXT("TEXT", 0),
    IMAGE("IMAGE", 1);

    private String value;
    private int code;

    MessageTypeEnum(String value, int code) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public int getCode() {
        return code;
    }

    public static MessageTypeEnum fromCode(int code) {
        return Arrays.stream(MessageTypeEnum.values())
                .filter(type -> type.code == code)
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported message type code."));
    }
}
