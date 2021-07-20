package usociety.manager.domain.enums;

public enum CardTypeEnum {

    CREDIT("CREDIT"),
    DEBIT("DEBIT");

    private String value;

    CardTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
