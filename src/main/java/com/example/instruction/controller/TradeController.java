package com.example.instruction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.instruction.service.TradeService;

@RestController
public class TradeController {
	
	 private final TradeService tradeService;

	    public TradeController(TradeService tradeService) {
	        this.tradeService = tradeService;
	    }

	    @PostMapping("/upload")
	    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
	        try {
	            tradeService.processFile(file);
	            return ResponseEntity.ok("File processed and published successfully");
	        } catch (Exception e) {
	            return ResponseEntity.badRequest().body("Error processing file: " + e.getMessage());
	        }
	    }

}
