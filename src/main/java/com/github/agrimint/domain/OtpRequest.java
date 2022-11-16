package com.github.agrimint.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A OtpRequest.
 */
@Entity
@Table(name = "otp_request")
public class OtpRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @NotNull
    @Column(name = "country_code", nullable = false)
    private String countryCode;

    @NotNull
    @Column(name = "otp", nullable = false)
    private String otp;

    @NotNull
    @Column(name = "otp_type", nullable = false)
    private String otpType;

    @NotNull
    @Column(name = "otp_length", nullable = false)
    private Integer otpLength;

    @NotNull
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "date_validated")
    private Instant dateValidated;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OtpRequest id(Long id) {
        this.id = id;
        return this;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public OtpRequest phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public OtpRequest countryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getOtp() {
        return this.otp;
    }

    public OtpRequest otp(String otp) {
        this.otp = otp;
        return this;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getOtpType() {
        return this.otpType;
    }

    public OtpRequest otpType(String otpType) {
        this.otpType = otpType;
        return this;
    }

    public void setOtpType(String otpType) {
        this.otpType = otpType;
    }

    public Integer getOtpLength() {
        return this.otpLength;
    }

    public OtpRequest otpLength(Integer otpLength) {
        this.otpLength = otpLength;
        return this;
    }

    public void setOtpLength(Integer otpLength) {
        this.otpLength = otpLength;
    }

    public String getStatus() {
        return this.status;
    }

    public OtpRequest status(String status) {
        this.status = status;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getDateValidated() {
        return this.dateValidated;
    }

    public OtpRequest dateValidated(Instant dateValidated) {
        this.dateValidated = dateValidated;
        return this;
    }

    public void setDateValidated(Instant dateValidated) {
        this.dateValidated = dateValidated;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OtpRequest)) {
            return false;
        }
        return id != null && id.equals(((OtpRequest) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OtpRequest{" +
            "id=" + getId() +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", countryCode='" + getCountryCode() + "'" +
            ", otp='" + getOtp() + "'" +
            ", otpType='" + getOtpType() + "'" +
            ", otpLength=" + getOtpLength() +
            ", status='" + getStatus() + "'" +
            ", dateValidated='" + getDateValidated() + "'" +
            "}";
    }
}
