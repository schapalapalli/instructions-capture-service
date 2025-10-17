package com.example.instruction.util;

import org.springframework.stereotype.Component;

import com.example.instruction.model.CanonicalTrade;
import com.example.instruction.model.PlatformTrade;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

@Component
public class TradeTransformer {
	
	private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	public CanonicalTrade toCanonical(CanonicalTrade input) {
		 
		
		 
		 CanonicalTrade canonical = new CanonicalTrade();
		 
		 // account_number: Mask all but last 4 digits.
		 	String account = input.getAccount_number();
		 	if (account != null && account.length() > 4) {
	            canonical.setAccount_number("*".repeat(account.length() - 4) + account.substring(account.length() - 4));
	        } else {
	            canonical.setAccount_number(account != null ? account : "");
	        }
		 // security_id: Convert to uppercase and validate format.
			 
	        String sec = input.getSecurity_id() != null ? input.getSecurity_id().toUpperCase() : "";
	        if (!sec.matches("^[A-Z0-9]+$")) {
	            throw new IllegalArgumentException("Invalid security_id format: " + sec);
	        }
	        canonical.setSecurity_id(sec);
	        //Normalize to standard codes
	        String type = input.getTrade_type() != null ? input.getTrade_type().toLowerCase() : "";
	        if ("buy".equals(type)) {
	            canonical.setTrade_type("B");
	        } else if ("sell".equals(type)) {
	            canonical.setTrade_type("S");
	        } else {
	            canonical.setTrade_type(type.toUpperCase());
	        }
	        // Pass through amount and timestamp
	        canonical.setAmount(input.getAmount());
	        canonical.setTimestamp(input.getTimestamp());
	        
	        return canonical;
		 
		 
		 }
	 
	 public String toPlatformJson(CanonicalTrade canonical) throws Exception {
	        PlatformTrade pt = new PlatformTrade();
	        PlatformTrade.TradeDetails tradeDetails = new PlatformTrade.TradeDetails();
	        tradeDetails.setAccount(canonical.getAccount_number());
	        tradeDetails.setSecurity(canonical.getSecurity_id());
	        tradeDetails.setType(canonical.getTrade_type());
	        tradeDetails.setAmount(canonical.getAmount());
	        tradeDetails.setTimestamp(canonical.getTimestamp());
	        pt.setTrade(tradeDetails);
	        return objectMapper.writeValueAsString(pt);
	    }
	 
	 public String sanitize(String input) {
	        if (input == null) return "";
	        // Remove potential injection chars, trim
	        return input.trim().replaceAll("[^a-zA-Z0-9*\\-:.+ ]", "");
	    }

	

	 
}