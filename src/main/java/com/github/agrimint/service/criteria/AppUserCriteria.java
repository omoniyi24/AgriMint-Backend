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
 * Criteria class for the {@link com.github.agrimint.domain.AppUser} entity. This class is used
 * in {@link com.github.agrimint.web.rest.AppUserResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /app-users?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class AppUserCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter login;

    private StringFilter name;

    private StringFilter phoneNumber;

    private StringFilter countryCode;

    private StringFilter secret;

    private BooleanFilter active;

    private InstantFilter dateCreated;

    private LongFilter authoritiesId;

    public AppUserCriteria() {}

    public AppUserCriteria(AppUserCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.login = other.login == null ? null : other.login.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.phoneNumber = other.phoneNumber == null ? null : other.phoneNumber.copy();
        this.countryCode = other.countryCode == null ? null : other.countryCode.copy();
        this.secret = other.secret == null ? null : other.secret.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.dateCreated = other.dateCreated == null ? null : other.dateCreated.copy();
        this.authoritiesId = other.authoritiesId == null ? null : other.authoritiesId.copy();
    }

    @Override
    public AppUserCriteria copy() {
        return new AppUserCriteria(this);
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

    public StringFilter getLogin() {
        return login;
    }

    public StringFilter login() {
        if (login == null) {
            login = new StringFilter();
        }
        return login;
    }

    public void setLogin(StringFilter login) {
        this.login = login;
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

    public StringFilter getSecret() {
        return secret;
    }

    public StringFilter secret() {
        if (secret == null) {
            secret = new StringFilter();
        }
        return secret;
    }

    public void setSecret(StringFilter secret) {
        this.secret = secret;
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

    public LongFilter getAuthoritiesId() {
        return authoritiesId;
    }

    public LongFilter authoritiesId() {
        if (authoritiesId == null) {
            authoritiesId = new LongFilter();
        }
        return authoritiesId;
    }

    public void setAuthoritiesId(LongFilter authoritiesId) {
        this.authoritiesId = authoritiesId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AppUserCriteria that = (AppUserCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(login, that.login) &&
            Objects.equals(name, that.name) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(countryCode, that.countryCode) &&
            Objects.equals(secret, that.secret) &&
            Objects.equals(active, that.active) &&
            Objects.equals(dateCreated, that.dateCreated) &&
            Objects.equals(authoritiesId, that.authoritiesId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, name, phoneNumber, countryCode, secret, active, dateCreated, authoritiesId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppUserCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (login != null ? "login=" + login + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (phoneNumber != null ? "phoneNumber=" + phoneNumber + ", " : "") +
            (countryCode != null ? "countryCode=" + countryCode + ", " : "") +
            (secret != null ? "secret=" + secret + ", " : "") +
            (active != null ? "active=" + active + ", " : "") +
            (dateCreated != null ? "dateCreated=" + dateCreated + ", " : "") +
            (authoritiesId != null ? "authoritiesId=" + authoritiesId + ", " : "") +
            "}";
    }
}
