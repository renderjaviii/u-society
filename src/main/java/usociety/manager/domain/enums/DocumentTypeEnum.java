package usociety.manager.domain.enums;

public enum DocumentTypeEnum {

    CC("CC"),
    NIT("NIT");

    private String value;

    DocumentTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
