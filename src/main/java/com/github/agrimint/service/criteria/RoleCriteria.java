package com.github.agrimint.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.github.agrimint.domain.Role} entity. This class is used
 * in {@link com.github.agrimint.web.rest.RoleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /roles?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class RoleCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter description;

    private BooleanFilter userDefined;

    private BooleanFilter defaultRole;

    private StringFilter roleGroup;

    private LongFilter authoritiesId;

    private LongFilter usersId;

    public RoleCriteria() {}

    public RoleCriteria(RoleCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.userDefined = other.userDefined == null ? null : other.userDefined.copy();
        this.defaultRole = other.defaultRole == null ? null : other.defaultRole.copy();
        this.roleGroup = other.roleGroup == null ? null : other.roleGroup.copy();
        this.authoritiesId = other.authoritiesId == null ? null : other.authoritiesId.copy();
        this.usersId = other.usersId == null ? null : other.usersId.copy();
    }

    @Override
    public RoleCriteria copy() {
        return new RoleCriteria(this);
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

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public BooleanFilter getUserDefined() {
        return userDefined;
    }

    public BooleanFilter userDefined() {
        if (userDefined == null) {
            userDefined = new BooleanFilter();
        }
        return userDefined;
    }

    public void setUserDefined(BooleanFilter userDefined) {
        this.userDefined = userDefined;
    }

    public BooleanFilter getDefaultRole() {
        return defaultRole;
    }

    public BooleanFilter defaultRole() {
        if (defaultRole == null) {
            defaultRole = new BooleanFilter();
        }
        return defaultRole;
    }

    public void setDefaultRole(BooleanFilter defaultRole) {
        this.defaultRole = defaultRole;
    }

    public StringFilter getRoleGroup() {
        return roleGroup;
    }

    public StringFilter roleGroup() {
        if (roleGroup == null) {
            roleGroup = new StringFilter();
        }
        return roleGroup;
    }

    public void setRoleGroup(StringFilter roleGroup) {
        this.roleGroup = roleGroup;
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

    public LongFilter getUsersId() {
        return usersId;
    }

    public LongFilter usersId() {
        if (usersId == null) {
            usersId = new LongFilter();
        }
        return usersId;
    }

    public void setUsersId(LongFilter usersId) {
        this.usersId = usersId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RoleCriteria that = (RoleCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(userDefined, that.userDefined) &&
            Objects.equals(defaultRole, that.defaultRole) &&
            Objects.equals(roleGroup, that.roleGroup) &&
            Objects.equals(authoritiesId, that.authoritiesId) &&
            Objects.equals(usersId, that.usersId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, userDefined, defaultRole, roleGroup, authoritiesId, usersId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RoleCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (userDefined != null ? "userDefined=" + userDefined + ", " : "") +
            (defaultRole != null ? "defaultRole=" + defaultRole + ", " : "") +
            (roleGroup != null ? "roleGroup=" + roleGroup + ", " : "") +
            (authoritiesId != null ? "authoritiesId=" + authoritiesId + ", " : "") +
            (usersId != null ? "usersId=" + usersId + ", " : "") +
            "}";
    }
}
