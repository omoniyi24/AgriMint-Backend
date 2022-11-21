package com.github.agrimint.service.criteria;

import com.github.agrimint.domain.enumeration.DRCR;
import com.github.agrimint.domain.enumeration.TransactionType;
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
 * Criteria class for the {@link com.github.agrimint.domain.Transactions} entity. This class is used
 * in {@link com.github.agrimint.web.rest.TransactionsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transactions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class TransactionsCriteria implements Serializable, Criteria {

    /**
     * Class for filtering DRCR
     */
    public static class DRCRFilter extends Filter<DRCR> {

        public DRCRFilter() {}

        public DRCRFilter(DRCRFilter filter) {
            super(filter);
        }

        @Override
        public DRCRFilter copy() {
            return new DRCRFilter(this);
        }
    }

    /**
     * Class for filtering TransactionType
     */
    public static class TransactionTypeFilter extends Filter<TransactionType> {

        public TransactionTypeFilter() {}

        public TransactionTypeFilter(TransactionTypeFilter filter) {
            super(filter);
        }

        @Override
        public TransactionTypeFilter copy() {
            return new TransactionTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter federationId;

    private StringFilter memberId;

    private StringFilter receiverId;

    private LongFilter amountInSat;

    private StringFilter description;

    private DRCRFilter drcr;

    private StringFilter transactionId;

    private InstantFilter transactionDate;

    private TransactionTypeFilter transactionType;

    public TransactionsCriteria() {}

    public TransactionsCriteria(TransactionsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.federationId = other.federationId == null ? null : other.federationId.copy();
        this.memberId = other.memberId == null ? null : other.memberId.copy();
        this.receiverId = other.receiverId == null ? null : other.receiverId.copy();
        this.amountInSat = other.amountInSat == null ? null : other.amountInSat.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.drcr = other.drcr == null ? null : other.drcr.copy();
        this.transactionId = other.transactionId == null ? null : other.transactionId.copy();
        this.transactionDate = other.transactionDate == null ? null : other.transactionDate.copy();
        this.transactionType = other.transactionType == null ? null : other.transactionType.copy();
    }

    @Override
    public TransactionsCriteria copy() {
        return new TransactionsCriteria(this);
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

    public LongFilter getFederationId() {
        return federationId;
    }

    public LongFilter federationId() {
        if (federationId == null) {
            federationId = new LongFilter();
        }
        return federationId;
    }

    public void setFederationId(LongFilter federationId) {
        this.federationId = federationId;
    }

    public StringFilter getMemberId() {
        return memberId;
    }

    public StringFilter memberId() {
        if (memberId == null) {
            memberId = new StringFilter();
        }
        return memberId;
    }

    public void setMemberId(StringFilter memberId) {
        this.memberId = memberId;
    }

    public StringFilter getReceiverId() {
        return receiverId;
    }

    public StringFilter receiverId() {
        if (receiverId == null) {
            receiverId = new StringFilter();
        }
        return receiverId;
    }

    public void setReceiverId(StringFilter receiverId) {
        this.receiverId = receiverId;
    }

    public LongFilter getAmountInSat() {
        return amountInSat;
    }

    public LongFilter amountInSat() {
        if (amountInSat == null) {
            amountInSat = new LongFilter();
        }
        return amountInSat;
    }

    public void setAmountInSat(LongFilter amountInSat) {
        this.amountInSat = amountInSat;
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

    public DRCRFilter getDrcr() {
        return drcr;
    }

    public DRCRFilter drcr() {
        if (drcr == null) {
            drcr = new DRCRFilter();
        }
        return drcr;
    }

    public void setDrcr(DRCRFilter drcr) {
        this.drcr = drcr;
    }

    public StringFilter getTransactionId() {
        return transactionId;
    }

    public StringFilter transactionId() {
        if (transactionId == null) {
            transactionId = new StringFilter();
        }
        return transactionId;
    }

    public void setTransactionId(StringFilter transactionId) {
        this.transactionId = transactionId;
    }

    public InstantFilter getTransactionDate() {
        return transactionDate;
    }

    public InstantFilter transactionDate() {
        if (transactionDate == null) {
            transactionDate = new InstantFilter();
        }
        return transactionDate;
    }

    public void setTransactionDate(InstantFilter transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionTypeFilter getTransactionType() {
        return transactionType;
    }

    public TransactionTypeFilter transactionType() {
        if (transactionType == null) {
            transactionType = new TransactionTypeFilter();
        }
        return transactionType;
    }

    public void setTransactionType(TransactionTypeFilter transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TransactionsCriteria that = (TransactionsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(federationId, that.federationId) &&
            Objects.equals(memberId, that.memberId) &&
            Objects.equals(receiverId, that.receiverId) &&
            Objects.equals(amountInSat, that.amountInSat) &&
            Objects.equals(description, that.description) &&
            Objects.equals(drcr, that.drcr) &&
            Objects.equals(transactionId, that.transactionId) &&
            Objects.equals(transactionDate, that.transactionDate) &&
            Objects.equals(transactionType, that.transactionType)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            federationId,
            memberId,
            receiverId,
            amountInSat,
            description,
            drcr,
            transactionId,
            transactionDate,
            transactionType
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionsCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (federationId != null ? "federationId=" + federationId + ", " : "") +
            (memberId != null ? "memberId=" + memberId + ", " : "") +
            (receiverId != null ? "receiverId=" + receiverId + ", " : "") +
            (amountInSat != null ? "amountInSat=" + amountInSat + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (drcr != null ? "drcr=" + drcr + ", " : "") +
            (transactionId != null ? "transactionId=" + transactionId + ", " : "") +
            (transactionDate != null ? "transactionDate=" + transactionDate + ", " : "") +
            (transactionType != null ? "transactionType=" + transactionType + ", " : "") +
            "}";
    }
}
