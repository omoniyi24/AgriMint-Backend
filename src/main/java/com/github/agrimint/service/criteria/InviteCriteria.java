package com.github.agrimint.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.github.agrimint.domain.Invite} entity. This class is used
 * in {@link com.github.agrimint.web.rest.InviteResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /invites?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class InviteCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter phoneNumber;

    private StringFilter countryCode;

    private StringFilter invitationCode;

    private LongFilter federationId;

    private BooleanFilter active;

    public InviteCriteria() {}

    public InviteCriteria(InviteCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.phoneNumber = other.phoneNumber == null ? null : other.phoneNumber.copy();
        this.countryCode = other.countryCode == null ? null : other.countryCode.copy();
        this.invitationCode = other.invitationCode == null ? null : other.invitationCode.copy();
        this.federationId = other.federationId == null ? null : other.federationId.copy();
        this.active = other.active == null ? null : other.active.copy();
    }

    @Override
    public InviteCriteria copy() {
        return new InviteCriteria(this);
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

    public StringFilter getPhoneNumber() {
        return phoneNumber;
    }

    public StringFilter phoneNumber() {
        if (phoneNumber == null) {
            phoneNumber = new StringFilter();
        }
        return phoneNumber;
    }

    public void setPhoneNumber(StringFilter phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public StringFilter getCountryCode() {
        return countryCode;
    }

    public StringFilter countryCode() {
        if (countryCode == null) {
            countryCode = new StringFilter();
        }
        return countryCode;
    }

    public void setCountryCode(StringFilter countryCode) {
        this.countryCode = countryCode;
    }

    public StringFilter getInvitationCode() {
        return invitationCode;
    }

    public StringFilter invitationCode() {
        if (invitationCode == null) {
            invitationCode = new StringFilter();
        }
        return invitationCode;
    }

    public void setInvitationCode(StringFilter invitationCode) {
        this.invitationCode = invitationCode;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final InviteCriteria that = (InviteCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(countryCode, that.countryCode) &&
            Objects.equals(invitationCode, that.invitationCode) &&
            Objects.equals(federationId, that.federationId) &&
            Objects.equals(active, that.active)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phoneNumber, countryCode, invitationCode, federationId, active);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InviteCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (phoneNumber != null ? "phoneNumber=" + phoneNumber + ", " : "") +
            (countryCode != null ? "countryCode=" + countryCode + ", " : "") +
            (invitationCode != null ? "invitationCode=" + invitationCode + ", " : "") +
            (federationId != null ? "federationId=" + federationId + ", " : "") +
            (active != null ? "active=" + active + ", " : "") +
            "}";
    }
}
