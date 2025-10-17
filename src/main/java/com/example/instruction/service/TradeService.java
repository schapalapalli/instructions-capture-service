package com.example.instruction.service;


import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVReaderHeaderAware;

import com.example.instruction.model.CanonicalTrade;
import com.example.instruction.util.TradeTransformer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class TradeService {
	

	private final ConcurrentHashMap<String, CanonicalTrade> storage = new ConcurrentHashMap<>();
    private final TradeTransformer transformer;
    private final KafkaPublisher kafkaPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public TradeService(TradeTransformer transformer, KafkaPublisher kafkaPublisher) {
        this.transformer = transformer;
        this.kafkaPublisher = kafkaPublisher;
    }
    
	public void processFile(MultipartFile file) throws Exception{
		// TODO Auto-generated method stub
		if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        List<CanonicalTrade> trades = parseFile(file);
        for (CanonicalTrade trade : trades) {
            processTrade(trade);
        }
	}
	public void processKafkaMessage(String message) throws Exception {
        CanonicalTrade trade = objectMapper.readValue(message, CanonicalTrade.class);
        processTrade(trade);
    }

    private void processTrade(CanonicalTrade trade) throws Exception {
        CanonicalTrade canonical = transformer.toCanonical(trade);
        if (canonical == null) {
            throw new IllegalStateException("Canonical trade is null after transformation");
        }
        String id = generateId(canonical);
        storage.put(id, canonical); 
        String json = transformer.toPlatformJson(canonical);
        kafkaPublisher.publish(json);
    }
	private List<CanonicalTrade> parseFile(MultipartFile file) throws Exception {
		// TODO Auto-generated method stub
		List<CanonicalTrade> trades =new ArrayList<>();
		String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("Invalid file name");
        }
        try (InputStream is = file.getInputStream()) {
        	if (filename.endsWith(".csv")) {
        	    
        	    try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
        	         CSVReaderHeaderAware csvReader = new CSVReaderHeaderAware(isr)) {

        	        Map<String, String> record;
        	        while ((record = csvReader.readMap()) != null) {
        	            CanonicalTrade trade = new CanonicalTrade();
        	            trade.setAccount_number(transformer.sanitize(record.get("account_number")));
        	            trade.setSecurity_id(transformer.sanitize(record.get("security_id")));
        	            trade.setTrade_type(transformer.sanitize(record.get("trade_type")));
        	            trade.setAmount(Long.parseLong(transformer.sanitize(record.get("amount"))));
        	            trade.setTimestamp(ZonedDateTime.parse(transformer.sanitize(record.get("timestamp"))));
        	            trades.add(trade);
        	        }

        	    } catch (Exception e) {
        	        // Handle exceptions here
        	        e.printStackTrace();
        	    }
        	}
            
            
            else if (filename.endsWith(".json")) {
                trades = objectMapper.readValue(is, new TypeReference<>() {});
            } else {
                throw new IllegalArgumentException("Unsupported file type");
            }
        }
		return trades;
	}
	private String generateId(CanonicalTrade trade) {
        String account = trade != null && trade.getAccount_number() != null ? trade.getAccount_number() : "";
        String security = trade != null && trade.getSecurity_id() != null ? trade.getSecurity_id() : "";
        return account + "_" + security;
    }

    public CanonicalTrade getStoredTrade(String id) {
        return storage.get(id);
    }
}
