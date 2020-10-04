package usociety.manager.domain.enums;

public enum UserGroupStatusEnum {
    ACTIVE("ACTIVE", 0),
    PENDING("PENDING", 1),
    REJECTED("REJECTED", 2),
    DELETED("DELETED", 4);

    private String value;
    private int code;

    UserGroupStatusEnum(String value, int code) {
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
