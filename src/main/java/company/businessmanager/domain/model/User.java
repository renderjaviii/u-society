package company.businessmanager.domain.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "document_number", unique = true, nullable = false, length = 10)
    private String documentNumber;

    @Column(name = "gender", length = 1, nullable = false)
    private String gender;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "last_access_at", nullable = false)
    private LocalDateTime lastAccessAt;

    @Column(name = "account_locked", nullable = false, insertable = false, columnDefinition = "boolean default false")
    private Boolean accountLocked;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "credential_id", referencedColumnName = "id")
    private Credential credential;

    public User() {
        super();
    }

    public User(User user) {
        this.id = user.id;
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.documentNumber = user.documentNumber;
        this.gender = user.gender;
        this.birthDate = user.birthDate;
        this.phoneNumber = user.phoneNumber;
        this.createdAt = user.createdAt;
        this.lastAccessAt = user.lastAccessAt;
        this.accountLocked = user.accountLocked;
        this.roles = user.roles;
        this.credential = user.credential;
    }

    private User(Builder builder) {
        id = builder.id;
        firstName = builder.firstName;
        lastName = builder.lastName;
        documentNumber = builder.documentNumber;
        gender = builder.gender;
        birthDate = builder.birthDate;
        phoneNumber = builder.phoneNumber;
        createdAt = builder.createdAt;
        lastAccessAt = builder.lastAccessAt;
        accountLocked = builder.accountLocked;
        roles = builder.roles;
        credential = builder.credential;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getGender() {
        return gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastAccessAt() {
        return lastAccessAt;
    }

    public Boolean getAccountLocked() {
        return accountLocked;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public Credential getCredential() {
        return credential;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User that = (User) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private Long id;
        private String firstName;
        private String lastName;
        private String documentNumber;
        private String gender;
        private LocalDate birthDate;
        private String phoneNumber;
        private LocalDate createdAt;
        private LocalDateTime lastAccessAt;
        private Boolean accountLocked;
        private List<Role> roles;
        private Credential credential;

        private Builder() {
            super();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder documentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder createdAt(LocalDate createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder lastAccessAt(LocalDateTime lastAccessAt) {
            this.lastAccessAt = lastAccessAt;
            return this;
        }

        public Builder accountLocked(Boolean accountLocked) {
            this.accountLocked = accountLocked;
            return this;
        }

        public Builder roles(List<Role> roles) {
            this.roles = roles;
            return this;
        }

        public Builder credential(Credential credential) {
            this.credential = credential;
            return this;
        }

        public User build() {
            return new User(this);
        }

    }

}