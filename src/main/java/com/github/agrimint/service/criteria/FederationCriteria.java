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
 * Criteria class for the {@link com.github.agrimint.domain.Federation} entity. This class is used
 * in {@link com.github.agrimint.web.rest.FederationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /federations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class FederationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter alias;

    private BooleanFilter active;

    private InstantFilter dateCreated;

    public FederationCriteria() {}

    public FederationCriteria(FederationCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.alias = other.alias == null ? null : other.alias.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.dateCreated = other.dateCreated == null ? null : other.dateCreated.copy();
    }

    @Override
    public FederationCriteria copy() {
        return new FederationCriteria(this);
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

    public StringFilter getAlias() {
        return alias;
    }

    public StringFilter alias() {
        if (alias == null) {
            alias = new StringFilter();
        }
        return alias;
    }

    public void setAlias(StringFilter alias) {
        this.alias = alias;
    }

    public BooleanFilter getActive() {
        return active;
    }

    public BooleanFilter active() {
        if (active == null) {
            active = new BooleanFilter();
        }
        return active;
    }

    public void setActive(BooleanFilter active) {
        this.active = active;
    }

    public InstantFilter getDateCreated() {
        return dateCreated;
    }

    public InstantFilter dateCreated() {
        if (dateCreated == null) {
            dateCreated = new InstantFilter();
        }
        return dateCreated;
    }

    public void setDateCreated(InstantFilter dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FederationCriteria that = (FederationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(alias, that.alias) &&
            Objects.equals(active, that.active) &&
            Objects.equals(dateCreated, that.dateCreated)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, alias, active, dateCreated);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FederationCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (alias != null ? "alias=" + alias + ", " : "") +
            (active != null ? "active=" + active + ", " : "") +
            (dateCreated != null ? "dateCreated=" + dateCreated + ", " : "") +
            "}";
    }
}
