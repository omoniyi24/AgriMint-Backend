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
 * Criteria class for the {@link com.github.agrimint.domain.OtpRequest} entity. This class is used
 * in {@link com.github.agrimint.web.rest.OtpRequestResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /otp-requests?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class OtpRequestCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter phoneNumber;

    private StringFilter countryCode;

    private StringFilter otp;

    private StringFilter otpType;

    private IntegerFilter otpLength;

    private StringFilter status;

    private InstantFilter dateValidated;

    public OtpRequestCriteria() {}

    public OtpRequestCriteria(OtpRequestCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.phoneNumber = other.phoneNumber == null ? null : other.phoneNumber.copy();
        this.countryCode = other.countryCode == null ? null : other.countryCode.copy();
        this.otp = other.otp == null ? null : other.otp.copy();
        this.otpType = other.otpType == null ? null : other.otpType.copy();
        this.otpLength = other.otpLength == null ? null : other.otpLength.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.dateValidated = other.dateValidated == null ? null : other.dateValidated.copy();
    }

    @Override
    public OtpRequestCriteria copy() {
        return new OtpRequestCriteria(this);
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

    public StringFilter getOtp() {
        return otp;
    }

    public StringFilter otp() {
        if (otp == null) {
            otp = new StringFilter();
        }
        return otp;
    }

    public void setOtp(StringFilter otp) {
        this.otp = otp;
    }

    public StringFilter getOtpType() {
        return otpType;
    }

    public StringFilter otpType() {
        if (otpType == null) {
            otpType = new StringFilter();
        }
        return otpType;
    }

    public void setOtpType(StringFilter otpType) {
        this.otpType = otpType;
    }

    public IntegerFilter getOtpLength() {
        return otpLength;
    }

    public IntegerFilter otpLength() {
        if (otpLength == null) {
            otpLength = new IntegerFilter();
        }
        return otpLength;
    }

    public void setOtpLength(IntegerFilter otpLength) {
        this.otpLength = otpLength;
    }

    public StringFilter getStatus() {
        return status;
    }

    public StringFilter status() {
        if (status == null) {
            status = new StringFilter();
        }
        return status;
    }

    public void setStatus(StringFilter status) {
        this.status = status;
    }

    public InstantFilter getDateValidated() {
        return dateValidated;
    }

    public InstantFilter dateValidated() {
        if (dateValidated == null) {
            dateValidated = new InstantFilter();
        }
        return dateValidated;
    }

    public void setDateValidated(InstantFilter dateValidated) {
        this.dateValidated = dateValidated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OtpRequestCriteria that = (OtpRequestCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(phoneNumber, that.phoneNumber) &&
            Objects.equals(countryCode, that.countryCode) &&
            Objects.equals(otp, that.otp) &&
            Objects.equals(otpType, that.otpType) &&
            Objects.equals(otpLength, that.otpLength) &&
            Objects.equals(status, that.status) &&
            Objects.equals(dateValidated, that.dateValidated)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phoneNumber, countryCode, otp, otpType, otpLength, status, dateValidated);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OtpRequestCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (phoneNumber != null ? "phoneNumber=" + phoneNumber + ", " : "") +
            (countryCode != null ? "countryCode=" + countryCode + ", " : "") +
            (otp != null ? "otp=" + otp + ", " : "") +
            (otpType != null ? "otpType=" + otpType + ", " : "") +
            (otpLength != null ? "otpLength=" + otpLength + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (dateValidated != null ? "dateValidated=" + dateValidated + ", " : "") +
            "}";
    }
}
