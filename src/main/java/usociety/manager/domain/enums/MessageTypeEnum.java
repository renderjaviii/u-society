package usociety.manager.domain.enums;

public enum MessageTypeEnum {

    TEXT("TEXT"),
    IMAGE("IMAGE");

    private String value;

    MessageTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
