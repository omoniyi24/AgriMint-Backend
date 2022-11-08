package com.github.agrimint.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Role.
 */
@Entity
@Table(name = "app_role")
public class Role implements Serializable {

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

    @NotNull
    @Column(name = "user_defined", nullable = false)
    private Boolean userDefined;

    @NotNull
    @Column(name = "default_role", nullable = false)
    private Boolean defaultRole;

    @Column(name = "role_group")
    private String roleGroup;

    @ManyToMany
    @JoinTable(
        name = "rel_app_role__authorities",
        joinColumns = @JoinColumn(name = "app_role_id"),
        inverseJoinColumns = @JoinColumn(name = "authorities_id")
    )
    @JsonIgnoreProperties(value = { "roles" }, allowSetters = true)
    private Set<Privilege> authorities = new HashSet<>();

    @ManyToMany(mappedBy = "authorities")
    @JsonIgnoreProperties(value = { "authorities" }, allowSetters = true)
    private Set<AppUser> users = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Role name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Role description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getUserDefined() {
        return this.userDefined;
    }

    public Role userDefined(Boolean userDefined) {
        this.userDefined = userDefined;
        return this;
    }

    public void setUserDefined(Boolean userDefined) {
        this.userDefined = userDefined;
    }

    public Boolean getDefaultRole() {
        return this.defaultRole;
    }

    public Role defaultRole(Boolean defaultRole) {
        this.defaultRole = defaultRole;
        return this;
    }

    public void setDefaultRole(Boolean defaultRole) {
        this.defaultRole = defaultRole;
    }

    public String getRoleGroup() {
        return this.roleGroup;
    }

    public Role roleGroup(String roleGroup) {
        this.roleGroup = roleGroup;
        return this;
    }

    public void setRoleGroup(String roleGroup) {
        this.roleGroup = roleGroup;
    }

    public Set<Privilege> getAuthorities() {
        return this.authorities;
    }

    public Role authorities(Set<Privilege> privileges) {
        this.setAuthorities(privileges);
        return this;
    }

    public Role addAuthorities(Privilege privilege) {
        this.authorities.add(privilege);
        privilege.getRoles().add(this);
        return this;
    }

    public Role removeAuthorities(Privilege privilege) {
        this.authorities.remove(privilege);
        privilege.getRoles().remove(this);
        return this;
    }

    public void setAuthorities(Set<Privilege> privileges) {
        this.authorities = privileges;
    }

    public Set<AppUser> getUsers() {
        return this.users;
    }

    public Role users(Set<AppUser> appUsers) {
        this.setUsers(appUsers);
        return this;
    }

    public Role addUsers(AppUser appUser) {
        this.users.add(appUser);
        appUser.getAuthorities().add(this);
        return this;
    }

    public Role removeUsers(AppUser appUser) {
        this.users.remove(appUser);
        appUser.getAuthorities().remove(this);
        return this;
    }

    public void setUsers(Set<AppUser> appUsers) {
        if (this.users != null) {
            this.users.forEach(i -> i.removeAuthorities(this));
        }
        if (appUsers != null) {
            appUsers.forEach(i -> i.addAuthorities(this));
        }
        this.users = appUsers;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Role)) {
            return false;
        }
        return id != null && id.equals(((Role) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Role{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", userDefined='" + getUserDefined() + "'" +
            ", defaultRole='" + getDefaultRole() + "'" +
            ", roleGroup='" + getRoleGroup() + "'" +
            "}";
    }
}
