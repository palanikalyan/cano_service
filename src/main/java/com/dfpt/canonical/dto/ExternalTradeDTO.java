package com.dfpt.canonical.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.opencsv.bean.CsvBindByName;

import java.math.BigDecimal;

@JacksonXmlRootElement(localName = "Order")
public class ExternalTradeDTO {

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

    // Getters and Setters
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
}
