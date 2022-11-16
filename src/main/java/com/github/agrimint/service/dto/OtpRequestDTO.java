package com.github.agrimint.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.github.agrimint.domain.OtpRequest} entity.
 */
public class OtpRequestDTO implements Serializable {

    private Long id;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String countryCode;

    @NotNull
    private String otp;

    @NotNull
    private String otpType;

    @NotNull
    private Integer otpLength;

    @NotNull
    private String status;

    private Instant dateValidated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getOtpType() {
        return otpType;
    }

    public void setOtpType(String otpType) {
        this.otpType = otpType;
    }

    public Integer getOtpLength() {
        return otpLength;
    }

    public void setOtpLength(Integer otpLength) {
        this.otpLength = otpLength;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getDateValidated() {
        return dateValidated;
    }

    public void setDateValidated(Instant dateValidated) {
        this.dateValidated = dateValidated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OtpRequestDTO)) {
            return false;
        }

        OtpRequestDTO otpRequestDTO = (OtpRequestDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, otpRequestDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OtpRequestDTO{" +
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
