package com.github.agrimint.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Member.
 */
@Entity
@Table(name = "member")
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "federation_id", nullable = false)
    private Long federationId;

    @Column(name = "fedimint_id")
    private String fedimintId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @NotNull
    @Column(name = "country_code", nullable = false)
    private String countryCode;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @NotNull
    @Column(name = "guardian", nullable = false)
    private Boolean guardian;

    @NotNull
    @Column(name = "date_created", nullable = false)
    private Instant dateCreated;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Member name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getFederationId() {
        return this.federationId;
    }

    public Member federationId(Long federationId) {
        this.federationId = federationId;
        return this;
    }

    public void setFederationId(Long federationId) {
        this.federationId = federationId;
    }

    public String getFedimintId() {
        return this.fedimintId;
    }

    public Member fedimintId(String fedimintId) {
        this.fedimintId = fedimintId;
        return this;
    }

    public void setFedimintId(String fedimintId) {
        this.fedimintId = fedimintId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public Member userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Member phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public Member countryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Member active(Boolean active) {
        this.active = active;
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getGuardian() {
        return this.guardian;
    }

    public Member guardian(Boolean guardian) {
        this.guardian = guardian;
        return this;
    }

    public void setGuardian(Boolean guardian) {
        this.guardian = guardian;
    }

    public Instant getDateCreated() {
        return this.dateCreated;
    }

    public Member dateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public void setDateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Member)) {
            return false;
        }
        return id != null && id.equals(((Member) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Member{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", federationId=" + getFederationId() +
            ", fedimintId='" + getFedimintId() + "'" +
            ", userId=" + getUserId() +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", countryCode='" + getCountryCode() + "'" +
            ", active='" + getActive() + "'" +
            ", guardian='" + getGuardian() + "'" +
            ", dateCreated='" + getDateCreated() + "'" +
            "}";
    }
}
