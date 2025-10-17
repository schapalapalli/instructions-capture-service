package com.example.instruction.model;


import java.time.ZonedDateTime;

public class PlatformTrade {
	
	
	
    private String platformId = "ACCT123"; 
    private TradeDetails trade;
	 
    public static class TradeDetails {
        private String account;
        private String security;
        private String type;
        private long amount;
        private ZonedDateTime timestamp;
        
	   
		
		public String getSecurity() {
			return security;
		}
		public void setSecurity(String security) {
			this.security = security;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
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
		public void setTimestamp(ZonedDateTime timestamp) {
			this.timestamp = timestamp;
		}
		public String getAccount() {
			return account;
		}
		public void setAccount(String account) {
			this.account = account;
		}
	

		 
	    
}

	public String getPlatformId() {
		return platformId;
	}

	public void setPlatformId(String platformId) {
		this.platformId = platformId;
	}

	public TradeDetails getTrade() {
		return trade;
	}

	public void setTrade(TradeDetails trade) {
		this.trade = trade;
	}

}