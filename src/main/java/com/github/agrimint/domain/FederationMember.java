package com.github.agrimint.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A FederationMember.
 */
@Entity
@Table(name = "federation_member")
public class FederationMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "federation_id", nullable = false)
    private Long federationId;

    @NotNull
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

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

    public FederationMember id(Long id) {
        this.id = id;
        return this;
    }

    public Long getFederationId() {
        return this.federationId;
    }

    public FederationMember federationId(Long federationId) {
        this.federationId = federationId;
        return this;
    }

    public void setFederationId(Long federationId) {
        this.federationId = federationId;
    }

    public Long getMemberId() {
        return this.memberId;
    }

    public FederationMember memberId(Long memberId) {
        this.memberId = memberId;
        return this;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Boolean getActive() {
        return this.active;
    }

    public FederationMember active(Boolean active) {
        this.active = active;
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getDateCreated() {
        return this.dateCreated;
    }

    public FederationMember dateCreated(Instant dateCreated) {
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
        if (!(o instanceof FederationMember)) {
            return false;
        }
        return id != null && id.equals(((FederationMember) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FederationMember{" +
            "id=" + getId() +
            ", federationId=" + getFederationId() +
            ", memberId=" + getMemberId() +
            ", active='" + getActive() + "'" +
            ", dateCreated='" + getDateCreated() + "'" +
            "}";
    }
}
