package com.aimons.jelivebien.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_paiement")
public class UserPaiement {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String externalReference;
    private String status;
    private String amount;
    private String currency;
    private String operator;
    private String code;
    private String operatorReference;
    private boolean paySuccess;
    private LocalDateTime datePaiement;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private User user_paiement;

    public UserPaiement() {
    }

    public UserPaiement(String externalReference, String status, String amount,
                        String currency, String operator,
                        String code, String operatorReference,
                        boolean paySuccess, User user_paiement,
                        LocalDateTime datePaiement) {
        this.externalReference = externalReference;
        this.status = status;
        this.amount = amount;
        this.currency = currency;
        this.operator = operator;
        this.code = code;
        this.operatorReference = operatorReference;
        this.paySuccess = paySuccess;
        this.user_paiement = user_paiement;
        this.datePaiement=datePaiement;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOperatorReference() {
        return operatorReference;
    }

    public void setOperatorReference(String operatorReference) {
        this.operatorReference = operatorReference;
    }

    public boolean isPaySuccess() {
        return paySuccess;
    }

    public void setPaySuccess(boolean paySuccess) {
        this.paySuccess = paySuccess;
    }

    public User getUser_paiement() {
        return user_paiement;
    }

    public void setUser_paiement(User user_paiement) {
        this.user_paiement = user_paiement;
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }
}
