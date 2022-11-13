package com.github.agrimint.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Federation.
 */
@Entity
@Table(name = "federation")
public class Federation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "fedimint_id")
    private String fedimintId;

    @NotNull
    @Column(name = "number_of_node", nullable = false)
    private Integer numberOfNode;

    @Column(name = "base_port")
    private Long basePort;

    @NotNull
    @Column(name = "number_of_registered_node", nullable = false)
    private Integer numberOfRegisteredNode;

    @Column(name = "created_by")
    private Long createdBy;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @NotNull
    @Column(name = "date_created", nullable = false)
    private Instant dateCreated;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Federation id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Federation name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFedimintId() {
        return this.fedimintId;
    }

    public Federation fedimintId(String fedimintId) {
        this.fedimintId = fedimintId;
        return this;
    }

    public void setFedimintId(String fedimintId) {
        this.fedimintId = fedimintId;
    }

    public Integer getNumberOfNode() {
        return this.numberOfNode;
    }

    public Federation numberOfNode(Integer numberOfNode) {
        this.numberOfNode = numberOfNode;
        return this;
    }

    public void setNumberOfNode(Integer numberOfNode) {
        this.numberOfNode = numberOfNode;
    }

    public Long getBasePort() {
        return this.basePort;
    }

    public Federation basePort(Long basePort) {
        this.basePort = basePort;
        return this;
    }

    public void setBasePort(Long basePort) {
        this.basePort = basePort;
    }

    public Integer getNumberOfRegisteredNode() {
        return this.numberOfRegisteredNode;
    }

    public Federation numberOfRegisteredNode(Integer numberOfRegisteredNode) {
        this.numberOfRegisteredNode = numberOfRegisteredNode;
        return this;
    }

    public void setNumberOfRegisteredNode(Integer numberOfRegisteredNode) {
        this.numberOfRegisteredNode = numberOfRegisteredNode;
    }

    public Long getCreatedBy() {
        return this.createdBy;
    }

    public Federation createdBy(Long createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Federation active(Boolean active) {
        this.active = active;
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getDateCreated() {
        return this.dateCreated;
    }

    public Federation dateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public void setDateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Federation)) {
            return false;
        }
        return id != null && id.equals(((Federation) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Federation{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", fedimintId='" + getFedimintId() + "'" +
            ", numberOfNode=" + getNumberOfNode() +
            ", basePort=" + getBasePort() +
            ", numberOfRegisteredNode=" + getNumberOfRegisteredNode() +
            ", createdBy=" + getCreatedBy() +
            ", active='" + getActive() + "'" +
            ", dateCreated='" + getDateCreated() + "'" +
            "}";
    }
}
