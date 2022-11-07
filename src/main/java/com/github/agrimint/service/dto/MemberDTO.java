package com.github.agrimint.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.github.agrimint.domain.Member} entity.
 */
public class MemberDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Long federationId;

    @NotNull
    private String fedimintId;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String countryCode;

    @NotNull
    private Boolean active;

    @NotNull
    private Boolean guardian;

    @NotNull
    private Instant dateCreated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getFederationId() {
        return federationId;
    }

    public void setFederationId(Long federationId) {
        this.federationId = federationId;
    }

    public String getFedimintId() {
        return fedimintId;
    }

    public void setFedimintId(String fedimintId) {
        this.fedimintId = fedimintId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getGuardian() {
        return guardian;
    }

    public void setGuardian(Boolean guardian) {
        this.guardian = guardian;
    }

    public Instant getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MemberDTO)) {
            return false;
        }

        MemberDTO memberDTO = (MemberDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, memberDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MemberDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", federationId=" + getFederationId() +
            ", fedimintId='" + getFedimintId() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", countryCode='" + getCountryCode() + "'" +
            ", active='" + getActive() + "'" +
            ", guardian='" + getGuardian() + "'" +
            ", dateCreated='" + getDateCreated() + "'" +
            "}";
    }
}
