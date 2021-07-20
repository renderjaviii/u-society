package usociety.manager.domain.enums;

public enum UserGroupStatusEnum {

    ACTIVE("ACTIVE"),
    PENDING("PENDING"),
    REJECTED("REJECTED"),
    DELETED("DELETED");

    private String value;

    UserGroupStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
