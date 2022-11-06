package com.github.agrimint.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.github.agrimint.domain.FederationMember} entity.
 */
public class FederationMemberDTO implements Serializable {

    private Long id;

    @NotNull
    private Long federationId;

    @NotNull
    private Long memberId;

    @NotNull
    private Boolean active;

    @NotNull
    private Instant dateCreated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFederationId() {
        return federationId;
    }

    public void setFederationId(Long federationId) {
        this.federationId = federationId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
        if (!(o instanceof FederationMemberDTO)) {
            return false;
        }

        FederationMemberDTO federationMemberDTO = (FederationMemberDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, federationMemberDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FederationMemberDTO{" +
            "id=" + getId() +
            ", federationId=" + getFederationId() +
            ", memberId=" + getMemberId() +
            ", active='" + getActive() + "'" +
            ", dateCreated='" + getDateCreated() + "'" +
            "}";
    }
}
