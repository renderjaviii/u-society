package common.manager.domain.enums;

public enum UserTypeEnum {

    ADMIN("admin", 1),
    STANDARD("standard", 0);

    private String value;
    private int code;

    UserTypeEnum(String value, int code) {
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
