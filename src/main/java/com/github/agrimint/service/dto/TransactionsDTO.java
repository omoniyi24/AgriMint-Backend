package com.github.agrimint.service.dto;

import com.github.agrimint.domain.enumeration.DRCR;
import com.github.agrimint.domain.enumeration.TransactionType;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.github.agrimint.domain.Transactions} entity.
 */
public class TransactionsDTO implements Serializable {

    private Long id;

    @NotNull
    private Long federationId;

    private String memberId;

    private String receiverId;

    @NotNull
    private Long amountInSat;

    @NotNull
    private String description;

    @NotNull
    private DRCR drcr;

    @NotNull
    private String transactionId;

    @NotNull
    private Instant transactionDate;

    @NotNull
    private TransactionType transactionType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFederationId() {
        return federationId;
    }

    public void setFederationId(Long federationId) {
        this.federationId = federationId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Long getAmountInSat() {
        return amountInSat;
    }

    public void setAmountInSat(Long amountInSat) {
        this.amountInSat = amountInSat;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DRCR getDrcr() {
        return drcr;
    }

    public void setDrcr(DRCR drcr) {
        this.drcr = drcr;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Instant getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionsDTO)) {
            return false;
        }

        TransactionsDTO transactionsDTO = (TransactionsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transactionsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionsDTO{" +
            "id=" + getId() +
            ", federationId=" + getFederationId() +
            ", memberId='" + getMemberId() + "'" +
            ", receiverId='" + getReceiverId() + "'" +
            ", amountInSat=" + getAmountInSat() +
            ", description='" + getDescription() + "'" +
            ", drcr='" + getDrcr() + "'" +
            ", transactionId='" + getTransactionId() + "'" +
            ", transactionDate='" + getTransactionDate() + "'" +
            ", transactionType='" + getTransactionType() + "'" +
            "}";
    }
}
