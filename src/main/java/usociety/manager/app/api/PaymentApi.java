package usociety.manager.app.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.GroupSequence;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import usociety.manager.app.util.BaseObject;
import usociety.manager.app.util.ExtraValidation;
import usociety.manager.app.util.validator.PaymentCreationConstraint;
import usociety.manager.domain.enums.CardTypeEnum;
import usociety.manager.domain.enums.DocumentTypeEnum;

@ApiModel("Payment")
@PaymentCreationConstraint(groups = ExtraValidation.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes( {
        @JsonSubTypes.Type(value = PaymentApi.CardPaymentApi.class, name = "card"),
        @JsonSubTypes.Type(value = PaymentApi.PSEPaymentApi.class, name = "pse")
})
@GroupSequence( { PaymentApi.class, ExtraValidation.class })
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
    @ApiModelProperty(notes = "Document type")
    @JsonProperty
    private DocumentTypeEnum documentType;

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

    public DocumentTypeEnum getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentTypeEnum documentType) {
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
        @ApiModelProperty(notes = "Card type")
        @JsonProperty
        private CardTypeEnum cardType;

        @NotEmpty
        @Pattern(regexp = "[\\d+]{16}")
        @ApiModelProperty(notes = "Card number")
        @JsonProperty
        private String cardNumber;

        @Pattern(regexp = "[\\d+]{4}")
        @ApiModelProperty(notes = "Card CVV")
        @JsonProperty
        private String cvv;

        @NotEmpty
        @Pattern(regexp = "[\\w\\d]+")
        @ApiModelProperty(notes = "Name on the card")
        @JsonProperty
        private String nameOnTheCard;

        @Min(1)
        @Max(36)
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
            cardType = builder.cardType;
            cardNumber = builder.cardNumber;
            cvv = builder.cvv;
            nameOnTheCard = builder.nameOnTheCard;
            quotes = builder.quotes;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public CardTypeEnum getCardType() {
            return cardType;
        }

        public String getCardNumber() {
            return cardNumber;
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
            private DocumentTypeEnum documentType;
            private LocalDateTime createdAt;
            private CardTypeEnum cardType;
            private String cardNumber;
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

            public Builder documentType(DocumentTypeEnum documentType) {
                this.documentType = documentType;
                return this;
            }

            public Builder createdAt(LocalDateTime createdAt) {
                this.createdAt = createdAt;
                return this;
            }

            public Builder cardType(CardTypeEnum cardType) {
                this.cardType = cardType;
                return this;
            }

            public Builder cardNumber(String cardNumber) {
                this.cardNumber = cardNumber;
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
        @ApiModelProperty(notes = "PSE Email")
        @JsonProperty
        private String pseEmail;

        @NotNull
        @Min(1)
        @Max(4)
        @ApiModelProperty(notes = "PSE Bank code")
        @JsonProperty
        private Integer pseBankCode;

        public PSEPaymentApi() {
            super();
        }

        private PSEPaymentApi(Builder builder) {
            setAmount(builder.amount);
            setDocumentNumber(builder.documentNumber);
            setDocumentType(builder.documentType);
            setCreatedAt(builder.createdAt);
            pseEmail = builder.pseEmail;
            pseBankCode = builder.pseBankCode;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public String getEmail() {
            return pseEmail;
        }

        public Integer getBankCode() {
            return pseBankCode;
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
            private DocumentTypeEnum documentType;
            private LocalDateTime createdAt;
            private String pseEmail;
            private Integer pseBankCode;

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

            public Builder documentType(DocumentTypeEnum documentType) {
                this.documentType = documentType;
                return this;
            }

            public Builder createdAt(LocalDateTime createdAt) {
                this.createdAt = createdAt;
                return this;
            }

            public Builder pseEmail(String pseEmail) {
                this.pseEmail = pseEmail;
                return this;
            }

            public Builder pseBankCode(Integer pseBankCode) {
                this.pseBankCode = pseBankCode;
                return this;
            }

            public PSEPaymentApi build() {
                return new PSEPaymentApi(this);
            }

        }

    }

}
