package ca.sheridancollege.yoojiw.bean;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class User {

	private int userId; 
	private String username; 
	private String password; 
	
	
	private int userAdditionId; 
	private double totalPayment; 
	private int totalPaymentCount; 
	private LocalDateTime registerDate; 
	
	
	
}
