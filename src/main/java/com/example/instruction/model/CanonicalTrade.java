package com.example.instruction.model;


import java.time.ZonedDateTime;

public class CanonicalTrade {
	private String account_number;
    private String security_id;
    private String trade_type;
    private long amount;
    private ZonedDateTime timestamp;
    
	public String getAccount_number() {
		return account_number;
	}
	public void setAccount_number(String account_number) {
		this.account_number = account_number;
	}
	public String getSecurity_id() {
		return security_id;
	}
	public void setSecurity_id(String security_id) {
		this.security_id = security_id;
	}
	public String getTrade_type() {
		return trade_type;
	}
	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public ZonedDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(ZonedDateTime zonedDateTime) {
		this.timestamp = zonedDateTime;
	}
    
	
    
    

}
