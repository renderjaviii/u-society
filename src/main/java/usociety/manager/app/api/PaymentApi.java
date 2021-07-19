package usociety.manager.app.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import usociety.manager.app.util.BaseObject;

@ApiModel("Payment")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes( {
        @JsonSubTypes.Type(value = PaymentApi.CardPaymentApi.class, name = "card"),
        @JsonSubTypes.Type(value = PaymentApi.PSEPaymentApi.class, name = "pse")
})
public abstract class PaymentApi extends BaseObject {

    @NotNull
    @Positive
    @Digits(integer = 17, fraction = 2)
    @ApiModelProperty(notes = "Amount")
    @JsonProperty
    private BigDecimal amount;

    @NotBlank
    @Pattern(regexp = "[\\d+]{7,11}")
    @ApiModelProperty(notes = "Document number")
    @JsonProperty
    private String documentNumber;

    @NotNull
    @Pattern(regexp = "CC|NIT")
    @ApiModelProperty(notes = "Document type")
    @JsonProperty
    private String documentType;

    @ApiModelProperty(notes = "Created at")
    @JsonProperty
    private LocalDateTime createdAt;

    protected PaymentApi() {
        super();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @ApiModel("Card payment data")
    public static class CardPaymentApi extends PaymentApi {

        @NotNull
        @Pattern(regexp = "CREDIT|DEBIT|CHECKING")
        @ApiModelProperty(notes = "Type")
        @JsonProperty
        private String type;

        @NotEmpty
        @Pattern(regexp = "[\\d+]{16}")
        @ApiModelProperty(notes = "Number")
        @JsonProperty
        private String number;

        @Pattern(regexp = "[\\d+]{3}")
        @ApiModelProperty(notes = "CVV")
        @JsonProperty
        private String cvv;

        @NotEmpty
        @Pattern(regexp = "[\\w\\d]+")
        @ApiModelProperty(notes = "Name on the card")
        @JsonProperty
        private String nameOnTheCard;

        @ApiModelProperty(notes = "Quotes")
        @JsonProperty
        private Integer quotes;

        public CardPaymentApi() {
            super();
        }

        private CardPaymentApi(Builder builder) {
            setAmount(builder.amount);
            setDocumentNumber(builder.documentNumber);
            setDocumentType(builder.documentType);
            setCreatedAt(builder.createdAt);
            type = builder.type;
            number = builder.number;
            cvv = builder.cvv;
            nameOnTheCard = builder.nameOnTheCard;
            quotes = builder.quotes;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public String getType() {
            return type;
        }

        public String getNumber() {
            return number;
        }

        public String getCvv() {
            return cvv;
        }

        public String getNameOnTheCard() {
            return nameOnTheCard;
        }

        public Integer getQuotes() {
            return quotes;
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        public static final class Builder {

            private BigDecimal amount;
            private String documentNumber;
            private String documentType;
            private LocalDateTime createdAt;
            private String type;
            private String number;
            private String cvv;
            private String nameOnTheCard;
            private Integer quotes;

            private Builder() {
                super();
            }

            public Builder amount(BigDecimal amount) {
                this.amount = amount;
                return this;
            }

            public Builder documentNumber(String documentNumber) {
                this.documentNumber = documentNumber;
                return this;
            }

            public Builder documentType(String documentType) {
                this.documentType = documentType;
                return this;
            }

            public Builder createdAt(LocalDateTime createdAt) {
                this.createdAt = createdAt;
                return this;
            }

            public Builder type(String type) {
                this.type = type;
                return this;
            }

            public Builder number(String number) {
                this.number = number;
                return this;
            }

            public Builder cvv(String cvv) {
                this.cvv = cvv;
                return this;
            }

            public Builder nameOnTheCard(String nameOnTheCard) {
                this.nameOnTheCard = nameOnTheCard;
                return this;
            }

            public Builder quotes(Integer quotes) {
                this.quotes = quotes;
                return this;
            }

            public CardPaymentApi build() {
                return new CardPaymentApi(this);
            }

        }

    }

    @ApiModel("PSE payment data")
    public static class PSEPaymentApi extends PaymentApi {

        @NotEmpty
        @Email
        @ApiModelProperty(notes = "Email")
        @JsonProperty
        private String email;

        @NotEmpty
        @Size(min = 1, max = 2)
        @ApiModelProperty(notes = "Bank code")
        @JsonProperty
        private String bankCode;

        public PSEPaymentApi() {
            super();
        }

        private PSEPaymentApi(Builder builder) {
            setAmount(builder.amount);
            setDocumentNumber(builder.documentNumber);
            setDocumentType(builder.documentType);
            setCreatedAt(builder.createdAt);
            email = builder.email;
            bankCode = builder.bankCode;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public String getEmail() {
            return email;
        }

        public String getBankCode() {
            return bankCode;
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        public static final class Builder {

            private BigDecimal amount;
            private String documentNumber;
            private String documentType;
            private LocalDateTime createdAt;
            private String email;
            private String bankCode;

            private Builder() {
                super();
            }

            public Builder amount(BigDecimal amount) {
                this.amount = amount;
                return this;
            }

            public Builder documentNumber(String documentNumber) {
                this.documentNumber = documentNumber;
                return this;
            }

            public Builder documentType(String documentType) {
                this.documentType = documentType;
                return this;
            }

            public Builder createdAt(LocalDateTime createdAt) {
                this.createdAt = createdAt;
                return this;
            }

            public Builder email(String email) {
                this.email = email;
                return this;
            }

            public Builder bankCode(String bankCode) {
                this.bankCode = bankCode;
                return this;
            }

            public PSEPaymentApi build() {
                return new PSEPaymentApi(this);
            }

        }

    }

}
