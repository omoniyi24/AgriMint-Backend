package com.github.agrimint.extended.dto;

import com.github.agrimint.service.dto.AppUserDTO;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.NotNull;

public class RefreshTokenDTO implements Serializable {

    private Long id;

    @NotNull
    private String token;

    @NotNull
    private Instant expiryDate;

    private AppUserDTO appUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public AppUserDTO getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUserDTO appUser) {
        this.appUser = appUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RefreshTokenDTO)) {
            return false;
        }

        RefreshTokenDTO refreshTokenDTO = (RefreshTokenDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, refreshTokenDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RefreshTokenDTO{" +
            "id=" + getId() +
            ", token='" + getToken() + "'" +
            ", expiryDate='" + getExpiryDate() + "'" +
            ", appUser=" + getAppUser() +
            "}";
    }
}
