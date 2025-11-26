package com.dfpt.canonical.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.opencsv.bean.CsvBindByName;

import java.math.BigDecimal;

@JacksonXmlRootElement(localName = "Order")
public class ExternalTradeDTO {

    // Original fields
    @CsvBindByName(column = "orderId")
    private String orderId;

    @CsvBindByName(column = "fundCode")
    private String fundCode;

    @CsvBindByName(column = "investorName")
    private String investorName;

    @CsvBindByName(column = "txnType")
    private String txnType;

    @CsvBindByName(column = "amount")
    private BigDecimal amount;

    @CsvBindByName(column = "units")
    private BigDecimal units;
    
    // New fields from fixed-width format
    @CsvBindByName(column = "originatorType")
    private String originatorType;
    
    @CsvBindByName(column = "firmNumber")
    private String firmNumber;
    
    @CsvBindByName(column = "fundNumber")
    private String fundNumber;
    
    @CsvBindByName(column = "transactionType")
    private String transactionType;
    
    @CsvBindByName(column = "transactionId")
    private String transactionId;
    
    @CsvBindByName(column = "tradeDate")
    private String tradeDate;
    
    @CsvBindByName(column = "dollarAmount")
    private BigDecimal dollarAmount;
    
    @CsvBindByName(column = "clientAccountNo")
    private String clientAccountNo;
    
    @CsvBindByName(column = "clientName")
    private String clientName;
    
    @CsvBindByName(column = "ssn")
    private String ssn;
    
    @CsvBindByName(column = "dob")
    private String dob;
    
    @CsvBindByName(column = "kyc")
    private String kyc;
    
    @CsvBindByName(column = "shareQuantity")
    private BigDecimal shareQuantity;


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

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
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
