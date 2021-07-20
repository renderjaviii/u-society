package usociety.manager.domain.enums;

public enum PostTypeEnum {

    TEXT("TEXT"),
    IMAGE("IMAGE"),
    SURVEY("SURVEY");

    private String value;

    PostTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
