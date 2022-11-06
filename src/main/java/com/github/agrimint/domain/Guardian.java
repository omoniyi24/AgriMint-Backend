package com.github.agrimint.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Guardian.
 */
@Entity
@Table(name = "guardian")
public class Guardian implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @NotNull
    @Column(name = "invitation_sent", nullable = false)
    private Boolean invitationSent;

    @NotNull
    @Column(name = "invitation_accepted", nullable = false)
    private Boolean invitationAccepted;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Guardian id(Long id) {
        this.id = id;
        return this;
    }

    public Long getMemberId() {
        return this.memberId;
    }

    public Guardian memberId(Long memberId) {
        this.memberId = memberId;
        return this;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Boolean getInvitationSent() {
        return this.invitationSent;
    }

    public Guardian invitationSent(Boolean invitationSent) {
        this.invitationSent = invitationSent;
        return this;
    }

    public void setInvitationSent(Boolean invitationSent) {
        this.invitationSent = invitationSent;
    }

    public Boolean getInvitationAccepted() {
        return this.invitationAccepted;
    }

    public Guardian invitationAccepted(Boolean invitationAccepted) {
        this.invitationAccepted = invitationAccepted;
        return this;
    }

    public void setInvitationAccepted(Boolean invitationAccepted) {
        this.invitationAccepted = invitationAccepted;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Guardian)) {
            return false;
        }
        return id != null && id.equals(((Guardian) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Guardian{" +
            "id=" + getId() +
            ", memberId=" + getMemberId() +
            ", invitationSent='" + getInvitationSent() + "'" +
            ", invitationAccepted='" + getInvitationAccepted() + "'" +
            "}";
    }
}
