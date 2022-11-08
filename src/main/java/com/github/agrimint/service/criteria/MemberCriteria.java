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
 * Criteria class for the {@link com.github.agrimint.domain.Member} entity. This class is used
 * in {@link com.github.agrimint.web.rest.MemberResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /members?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class MemberCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private LongFilter federationId;

    private StringFilter fedimintId;

    private LongFilter userId;

    private StringFilter phoneNumber;

    private StringFilter countryCode;

    private BooleanFilter active;

    private BooleanFilter guardian;

    private InstantFilter dateCreated;

    public MemberCriteria() {}

    public MemberCriteria(MemberCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.federationId = other.federationId == null ? null : other.federationId.copy();
        this.fedimintId = other.fedimintId == null ? null : other.fedimintId.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.phoneNumber = other.phoneNumber == null ? null : other.phoneNumber.copy();
        this.countryCode = other.countryCode == null ? null : other.countryCode.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.guardian = other.guardian == null ? null : other.guardian.copy();
        this.dateCreated = other.dateCreated == null ? null : other.dateCreated.copy();
    }

    @Override
    public MemberCriteria copy() {
        return new MemberCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
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

    public StringFilter getFedimintId() {
        return fedimintId;
    }

    public StringFilter fedimintId() {
        if (fedimintId == null) {
            fedimintId = new StringFilter();
        }
        return fedimintId;
    }

    public void setFedimintId(StringFilter fedimintId) {
        this.fedimintId = fedimintId;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public LongFilter userId() {
        if (userId == null) {
            userId = new LongFilter();
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
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

    public BooleanFilter getGuardian() {
        return guardian;
    }

    public BooleanFilter guardian() {
        if (guardian == null) {
            guardian = new BooleanFilter();
        }
        return guardian;
    }

    public void setGuardian(BooleanFilter guardian) {
        this.guardian = guardian;
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
        final MemberCriteria that = (MemberCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(federationId, that.federationId) &&
            Objects.equals(fedimintId, that.fedimintId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(countryCode, that.countryCode) &&
            Objects.equals(active, that.active) &&
            Objects.equals(guardian, that.guardian) &&
            Objects.equals(dateCreated, that.dateCreated)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, federationId, fedimintId, userId, phoneNumber, countryCode, active, guardian, dateCreated);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MemberCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (federationId != null ? "federationId=" + federationId + ", " : "") +
            (fedimintId != null ? "fedimintId=" + fedimintId + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (phoneNumber != null ? "phoneNumber=" + phoneNumber + ", " : "") +
            (countryCode != null ? "countryCode=" + countryCode + ", " : "") +
            (active != null ? "active=" + active + ", " : "") +
            (guardian != null ? "guardian=" + guardian + ", " : "") +
            (dateCreated != null ? "dateCreated=" + dateCreated + ", " : "") +
            "}";
    }
}
