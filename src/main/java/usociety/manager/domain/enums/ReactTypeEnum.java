package usociety.manager.domain.enums;

public enum ReactTypeEnum {

    LIKE("LIKE"),
    DISLIKE("DISLIKE"),
    ANGRY("ANGRY"),
    LAUGH("LAUGH");

    private String value;

    ReactTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
