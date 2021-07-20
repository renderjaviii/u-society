package usociety.manager.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("pse")
public class PSEPayment extends Payment {

    @Column(name = "pse_mail")
    private String pseEmail;

    @Column(name = "pse_bank_code", columnDefinition = "TINYINT")
    private Integer pseBankCode;

    public PSEPayment() {
        super();
    }

    private PSEPayment(Builder builder) {
        setId(builder.id);
        setAmount(builder.amount);
        setDocumentNumber(builder.documentNumber);
        setDocumentType(builder.documentType);
        setCreatedAt(builder.createdAt);
        setBusinessDate(builder.businessDate);
        pseEmail = builder.pseEmail;
        pseBankCode = builder.pseBankCode;
    }

    public String getPseEmail() {
        return pseEmail;
    }

    public Integer getPseBankCode() {
        return pseBankCode;
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
        private String pseEmail;
        private Integer pseBankCode;

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

        public Builder pseEmail(String pseEmail) {
            this.pseEmail = pseEmail;
            return this;
        }

        public Builder pseBankCode(Integer pseBankCode) {
            this.pseBankCode = pseBankCode;
            return this;
        }

        public PSEPayment build() {
            return new PSEPayment(this);
        }

    }

}
