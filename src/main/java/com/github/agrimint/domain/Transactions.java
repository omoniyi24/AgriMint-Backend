package com.github.agrimint.domain;

import com.github.agrimint.domain.enumeration.DRCR;
import com.github.agrimint.domain.enumeration.TransactionType;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Transactions.
 */
@Entity
@Table(name = "transactions")
public class Transactions implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "federation_id", nullable = false)
    private Long federationId;

    @Column(name = "member_id")
    private String memberId;

    @Column(name = "receiver_id")
    private String receiverId;

    @NotNull
    @Column(name = "amount_in_sat", nullable = false)
    private Long amountInSat;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "drcr", nullable = false)
    private DRCR drcr;

    @NotNull
    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @NotNull
    @Column(name = "transaction_date", nullable = false)
    private Instant transactionDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Transactions id(Long id) {
        this.id = id;
        return this;
    }

    public Long getFederationId() {
        return this.federationId;
    }

    public Transactions federationId(Long federationId) {
        this.federationId = federationId;
        return this;
    }

    public void setFederationId(Long federationId) {
        this.federationId = federationId;
    }

    public String getMemberId() {
        return this.memberId;
    }

    public Transactions memberId(String memberId) {
        this.memberId = memberId;
        return this;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getReceiverId() {
        return this.receiverId;
    }

    public Transactions receiverId(String receiverId) {
        this.receiverId = receiverId;
        return this;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Long getAmountInSat() {
        return this.amountInSat;
    }

    public Transactions amountInSat(Long amountInSat) {
        this.amountInSat = amountInSat;
        return this;
    }

    public void setAmountInSat(Long amountInSat) {
        this.amountInSat = amountInSat;
    }

    public String getDescription() {
        return this.description;
    }

    public Transactions description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DRCR getDrcr() {
        return this.drcr;
    }

    public Transactions drcr(DRCR drcr) {
        this.drcr = drcr;
        return this;
    }

    public void setDrcr(DRCR drcr) {
        this.drcr = drcr;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public Transactions transactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Instant getTransactionDate() {
        return this.transactionDate;
    }

    public Transactions transactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
        return this;
    }

    public void setTransactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionType getTransactionType() {
        return this.transactionType;
    }

    public Transactions transactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transactions)) {
            return false;
        }
        return id != null && id.equals(((Transactions) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Transactions{" +
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
