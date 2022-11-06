package com.github.agrimint.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.github.agrimint.domain.Federation} entity.
 */
public class FederationDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String alias;

    @NotNull
    private Boolean active;

    @NotNull
    private Instant dateCreated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FederationDTO)) {
            return false;
        }

        FederationDTO federationDTO = (FederationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, federationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FederationDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", alias='" + getAlias() + "'" +
            ", active='" + getActive() + "'" +
            ", dateCreated='" + getDateCreated() + "'" +
            "}";
    }
}
