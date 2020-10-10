package usociety.manager.domain.enums;

import java.util.Arrays;

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

    public static UserGroupStatusEnum fromCode(int code) {
        return Arrays.stream(UserGroupStatusEnum.values())
                .filter(type -> type.code == code)
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported user group status code."));
    }
}
