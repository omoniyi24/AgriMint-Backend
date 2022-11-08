package com.github.agrimint.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Privilege.
 */
@Entity
@Table(name = "app_privilege")
public class Privilege implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "jhi_group")
    private String group;

    @ManyToMany(mappedBy = "authorities")
    @JsonIgnoreProperties(value = { "authorities", "users" }, allowSetters = true)
    private Set<Role> roles = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Privilege id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Privilege name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Privilege description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroup() {
        return this.group;
    }

    public Privilege group(String group) {
        this.group = group;
        return this;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Set<Role> getRoles() {
        return this.roles;
    }

    public Privilege roles(Set<Role> roles) {
        this.setRoles(roles);
        return this;
    }

    public Privilege addRoles(Role role) {
        this.roles.add(role);
        role.getAuthorities().add(this);
        return this;
    }

    public Privilege removeRoles(Role role) {
        this.roles.remove(role);
        role.getAuthorities().remove(this);
        return this;
    }

    public void setRoles(Set<Role> roles) {
        if (this.roles != null) {
            this.roles.forEach(i -> i.removeAuthorities(this));
        }
        if (roles != null) {
            roles.forEach(i -> i.addAuthorities(this));
        }
        this.roles = roles;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Privilege)) {
            return false;
        }
        return id != null && id.equals(((Privilege) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Privilege{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", group='" + getGroup() + "'" +
            "}";
    }
}
