package usociety.manager.domain.enums;

public enum PostTypeEnum {
    TEXT("TEXT", 0),
    IMAGE("IMAGE", 1),
    SURVEY("SURVEY", 2);

    private String value;
    private int code;

    PostTypeEnum(String value, int code) {
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
