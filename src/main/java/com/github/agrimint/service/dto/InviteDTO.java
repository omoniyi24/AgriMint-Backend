package com.github.agrimint.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.github.agrimint.domain.Invite} entity.
 */
public class InviteDTO implements Serializable {

    private Long id;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String countryCode;

    @NotNull
    private String invitationCode;

    @NotNull
    private Long federationId;

    @NotNull
    private Boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public Long getFederationId() {
        return federationId;
    }

    public void setFederationId(Long federationId) {
        this.federationId = federationId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InviteDTO)) {
            return false;
        }

        InviteDTO inviteDTO = (InviteDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, inviteDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InviteDTO{" +
            "id=" + getId() +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", countryCode='" + getCountryCode() + "'" +
            ", invitationCode='" + getInvitationCode() + "'" +
            ", federationId=" + getFederationId() +
            ", active='" + getActive() + "'" +
            "}";
    }
}
