package com.github.agrimint.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.github.agrimint.domain.Guardian} entity.
 */
public class GuardianDTO implements Serializable {

    private Long id;

    @NotNull
    private Long memberId;

    @NotNull
    private Integer nodeNumber;

    @NotNull
    private Integer secret;

    @NotNull
    private Boolean invitationSent;

    @NotNull
    private Boolean invitationAccepted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Integer getNodeNumber() {
        return nodeNumber;
    }

    public void setNodeNumber(Integer nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    public Integer getSecret() {
        return secret;
    }

    public void setSecret(Integer secret) {
        this.secret = secret;
    }

    public Boolean getInvitationSent() {
        return invitationSent;
    }

    public void setInvitationSent(Boolean invitationSent) {
        this.invitationSent = invitationSent;
    }

    public Boolean getInvitationAccepted() {
        return invitationAccepted;
    }

    public void setInvitationAccepted(Boolean invitationAccepted) {
        this.invitationAccepted = invitationAccepted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GuardianDTO)) {
            return false;
        }

        GuardianDTO guardianDTO = (GuardianDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, guardianDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GuardianDTO{" +
            "id=" + getId() +
            ", memberId=" + getMemberId() +
            ", nodeNumber=" + getNodeNumber() +
            ", secret=" + getSecret() +
            ", invitationSent='" + getInvitationSent() + "'" +
            ", invitationAccepted='" + getInvitationAccepted() + "'" +
            "}";
    }
}
