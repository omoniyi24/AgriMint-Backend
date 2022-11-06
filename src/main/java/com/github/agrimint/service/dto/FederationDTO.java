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

    @NotNull
    private String fedimintId;

    @NotNull
    private Integer numberOfNode;

    @NotNull
    private Long basePort;

    @NotNull
    private Integer numberOfRegisteredNode;

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

    public String getFedimintId() {
        return fedimintId;
    }

    public void setFedimintId(String fedimintId) {
        this.fedimintId = fedimintId;
    }

    public Integer getNumberOfNode() {
        return numberOfNode;
    }

    public void setNumberOfNode(Integer numberOfNode) {
        this.numberOfNode = numberOfNode;
    }

    public Long getBasePort() {
        return basePort;
    }

    public void setBasePort(Long basePort) {
        this.basePort = basePort;
    }

    public Integer getNumberOfRegisteredNode() {
        return numberOfRegisteredNode;
    }

    public void setNumberOfRegisteredNode(Integer numberOfRegisteredNode) {
        this.numberOfRegisteredNode = numberOfRegisteredNode;
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
            ", fedimintId='" + getFedimintId() + "'" +
            ", numberOfNode=" + getNumberOfNode() +
            ", basePort=" + getBasePort() +
            ", numberOfRegisteredNode=" + getNumberOfRegisteredNode() +
            ", active='" + getActive() + "'" +
            ", dateCreated='" + getDateCreated() + "'" +
            "}";
    }
}
