package com.github.agrimint.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.github.agrimint.domain.FederationMember} entity. This class is used
 * in {@link com.github.agrimint.web.rest.FederationMemberResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /federation-members?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class FederationMemberCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter federationId;

    private LongFilter memberId;

    private BooleanFilter active;

    private InstantFilter dateCreated;

    public FederationMemberCriteria() {}

    public FederationMemberCriteria(FederationMemberCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.federationId = other.federationId == null ? null : other.federationId.copy();
        this.memberId = other.memberId == null ? null : other.memberId.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.dateCreated = other.dateCreated == null ? null : other.dateCreated.copy();
    }

    @Override
    public FederationMemberCriteria copy() {
        return new FederationMemberCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getFederationId() {
        return federationId;
    }

    public LongFilter federationId() {
        if (federationId == null) {
            federationId = new LongFilter();
        }
        return federationId;
    }

    public void setFederationId(LongFilter federationId) {
        this.federationId = federationId;
    }

    public LongFilter getMemberId() {
        return memberId;
    }

    public LongFilter memberId() {
        if (memberId == null) {
            memberId = new LongFilter();
        }
        return memberId;
    }

    public void setMemberId(LongFilter memberId) {
        this.memberId = memberId;
    }

    public BooleanFilter getActive() {
        return active;
    }

    public BooleanFilter active() {
        if (active == null) {
            active = new BooleanFilter();
        }
        return active;
    }

    public void setActive(BooleanFilter active) {
        this.active = active;
    }

    public InstantFilter getDateCreated() {
        return dateCreated;
    }

    public InstantFilter dateCreated() {
        if (dateCreated == null) {
            dateCreated = new InstantFilter();
        }
        return dateCreated;
    }

    public void setDateCreated(InstantFilter dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FederationMemberCriteria that = (FederationMemberCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(federationId, that.federationId) &&
            Objects.equals(memberId, that.memberId) &&
            Objects.equals(active, that.active) &&
            Objects.equals(dateCreated, that.dateCreated)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, federationId, memberId, active, dateCreated);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FederationMemberCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (federationId != null ? "federationId=" + federationId + ", " : "") +
            (memberId != null ? "memberId=" + memberId + ", " : "") +
            (active != null ? "active=" + active + ", " : "") +
            (dateCreated != null ? "dateCreated=" + dateCreated + ", " : "") +
            "}";
    }
}
