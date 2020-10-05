package usociety.manager.domain.enums;

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
}
