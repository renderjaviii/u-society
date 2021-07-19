package usociety.manager.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("card")
public class CardPayment extends Payment {

    @Column(name = "cardType", length = 10)
    private String cardType;

    @Column(name = "cardNumber", columnDefinition = "CHAR(16)")
    private String cardNumber;

    @Column(name = "cvv", columnDefinition = "CHAR(4)")
    private String cvv;

    @Column(name = "nameOnTheCard")
    private String nameOnTheCard;

    @Column(name = "quotes", columnDefinition = "TINYINT")
    private Integer quotes;

    public CardPayment() {
        super();
    }

    private CardPayment(Builder builder) {
        setId(builder.id);
        setAmount(builder.amount);
        setDocumentNumber(builder.documentNumber);
        setDocumentType(builder.documentType);
        setCreatedAt(builder.createdAt);
        setBusinessDate(builder.businessDate);
        cardType = builder.cardType;
        cardNumber = builder.cardNumber;
        cvv = builder.cvv;
        nameOnTheCard = builder.nameOnTheCard;
        quotes = builder.quotes;
    }

    public String getCardType() {
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
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private UUID id;
        private BigDecimal amount;
        private String documentNumber;
        private String documentType;
        private LocalDateTime createdAt;
        private LocalDate businessDate;
        private String cardType;
        private String cardNumber;
        private String cvv;
        private String nameOnTheCard;
        private Integer quotes;

        private Builder() {
            super();
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
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

        public Builder businessDate(LocalDate businessDate) {
            this.businessDate = businessDate;
            return this;
        }

        public Builder cardType(String cardType) {
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

        public CardPayment build() {
            return new CardPayment(this);
        }

    }

}
