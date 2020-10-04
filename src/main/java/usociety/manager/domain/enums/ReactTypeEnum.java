package usociety.manager.domain.enums;

public enum ReactTypeEnum {
    LIKE("LIKE", 0),
    DISLIKE("DISLIKE", 1),
    ANGRY("ANGRY", 2),
    LAUGH("LAUGH", 3);

    private String value;
    private int code;

    ReactTypeEnum(String value, int code) {
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
