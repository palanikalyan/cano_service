package com.dfpt.canonical.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "canonical_trades")
public class CanonicalTrade {

    @Id
    private UUID id;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "fund_code")
    private String fundCode;

    @Column(name = "investor_name")
    private String investorName;

    @Column(name = "txn_type")
    private String txnType;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "units")
    private BigDecimal units;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // New fields from fixed-width format
    @Column(name = "originator_type")
    private String originatorType;
    
    @Column(name = "firm_number")
    private String firmNumber;
    
    @Column(name = "fund_number")
    private String fundNumber;
    
    @Column(name = "transaction_type")
    private String transactionType;
    
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Column(name = "trade_date")
    private LocalDate tradeDate;
    
    @Column(name = "dollar_amount")
    private BigDecimal dollarAmount;
    
    @Column(name = "client_account_no")
    private String clientAccountNo;
    
    @Column(name = "client_name")
    private String clientName;
    
    @Column(name = "ssn")
    private String ssn;
    
    @Column(name = "dob")
    private LocalDate dob;
    
    @Column(name = "kyc")
    private String kyc;
    
    @Column(name = "share_quantity")
    private BigDecimal shareQuantity;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getFundCode() {
        return fundCode;
    }

    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }

    public String getInvestorName() {
        return investorName;
    }

    public void setInvestorName(String investorName) {
        this.investorName = investorName;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getUnits() {
        return units;
    }

    public void setUnits(BigDecimal units) {
        this.units = units;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getOriginatorType() {
        return originatorType;
    }

    public void setOriginatorType(String originatorType) {
        this.originatorType = originatorType;
    }

    public String getFirmNumber() {
        return firmNumber;
    }

    public void setFirmNumber(String firmNumber) {
        this.firmNumber = firmNumber;
    }

    public String getFundNumber() {
        return fundNumber;
    }

    public void setFundNumber(String fundNumber) {
        this.fundNumber = fundNumber;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }

    public BigDecimal getDollarAmount() {
        return dollarAmount;
    }

    public void setDollarAmount(BigDecimal dollarAmount) {
        this.dollarAmount = dollarAmount;
    }

    public String getClientAccountNo() {
        return clientAccountNo;
    }

    public void setClientAccountNo(String clientAccountNo) {
        this.clientAccountNo = clientAccountNo;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getKyc() {
        return kyc;
    }

    public void setKyc(String kyc) {
        this.kyc = kyc;
    }

    public BigDecimal getShareQuantity() {
        return shareQuantity;
    }

    public void setShareQuantity(BigDecimal shareQuantity) {
        this.shareQuantity = shareQuantity;
    }
}
