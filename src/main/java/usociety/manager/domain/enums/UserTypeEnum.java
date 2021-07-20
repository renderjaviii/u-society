package usociety.manager.domain.enums;

public enum UserTypeEnum {

    ADMIN("ADMIN"),
    STANDARD("STANDARD");

    private String value;

    UserTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
