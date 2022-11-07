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
 * Criteria class for the {@link com.github.agrimint.domain.Guardian} entity. This class is used
 * in {@link com.github.agrimint.web.rest.GuardianResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /guardians?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class GuardianCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter memberId;

    private IntegerFilter nodeNumber;

    private IntegerFilter secret;

    private BooleanFilter invitationSent;

    private BooleanFilter invitationAccepted;

    public GuardianCriteria() {}

    public GuardianCriteria(GuardianCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.memberId = other.memberId == null ? null : other.memberId.copy();
        this.nodeNumber = other.nodeNumber == null ? null : other.nodeNumber.copy();
        this.secret = other.secret == null ? null : other.secret.copy();
        this.invitationSent = other.invitationSent == null ? null : other.invitationSent.copy();
        this.invitationAccepted = other.invitationAccepted == null ? null : other.invitationAccepted.copy();
    }

    @Override
    public GuardianCriteria copy() {
        return new GuardianCriteria(this);
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

    public LongFilter getMemberId() {
        return memberId;
    }

    public LongFilter memberId() {
        if (memberId == null) {
            memberId = new LongFilter();
        }
        return memberId;
    }

    public void setMemberId(LongFilter memberId) {
        this.memberId = memberId;
    }

    public IntegerFilter getNodeNumber() {
        return nodeNumber;
    }

    public IntegerFilter nodeNumber() {
        if (nodeNumber == null) {
            nodeNumber = new IntegerFilter();
        }
        return nodeNumber;
    }

    public void setNodeNumber(IntegerFilter nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    public IntegerFilter getSecret() {
        return secret;
    }

    public IntegerFilter secret() {
        if (secret == null) {
            secret = new IntegerFilter();
        }
        return secret;
    }

    public void setSecret(IntegerFilter secret) {
        this.secret = secret;
    }

    public BooleanFilter getInvitationSent() {
        return invitationSent;
    }

    public BooleanFilter invitationSent() {
        if (invitationSent == null) {
            invitationSent = new BooleanFilter();
        }
        return invitationSent;
    }

    public void setInvitationSent(BooleanFilter invitationSent) {
        this.invitationSent = invitationSent;
    }

    public BooleanFilter getInvitationAccepted() {
        return invitationAccepted;
    }

    public BooleanFilter invitationAccepted() {
        if (invitationAccepted == null) {
            invitationAccepted = new BooleanFilter();
        }
        return invitationAccepted;
    }

    public void setInvitationAccepted(BooleanFilter invitationAccepted) {
        this.invitationAccepted = invitationAccepted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final GuardianCriteria that = (GuardianCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(memberId, that.memberId) &&
            Objects.equals(nodeNumber, that.nodeNumber) &&
            Objects.equals(secret, that.secret) &&
            Objects.equals(invitationSent, that.invitationSent) &&
            Objects.equals(invitationAccepted, that.invitationAccepted)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, memberId, nodeNumber, secret, invitationSent, invitationAccepted);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GuardianCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (memberId != null ? "memberId=" + memberId + ", " : "") +
            (nodeNumber != null ? "nodeNumber=" + nodeNumber + ", " : "") +
            (secret != null ? "secret=" + secret + ", " : "") +
            (invitationSent != null ? "invitationSent=" + invitationSent + ", " : "") +
            (invitationAccepted != null ? "invitationAccepted=" + invitationAccepted + ", " : "") +
            "}";
    }
}
