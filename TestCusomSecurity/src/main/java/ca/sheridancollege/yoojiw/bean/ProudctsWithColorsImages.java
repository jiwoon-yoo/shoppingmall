package ca.sheridancollege.yoojiw.bean;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ProudctsWithColorsImages {

	private int productId; 
	private int colorId; 
	private int imageId; 
	private int categoryId; 
	private int stockId;
	
	private String categoryName; 
	private String imageUrl; 
	private String productName; 
	private double price; 
	private String detail;
	
	private String colorHexaCode; 
	private String colorName;
	
	private String sizeName; 
	private int cartId; 
	private int sizeId; 
	
	private int quantity; 		//cartItems quantity 
	
	
	private int orderItemId; 		///
	private String recipientName;  
	private LocalDateTime orderDate; 
	private String shippingStatus; 			////주의. enum type value -> String type for db, template 
	
	private int orderId; 
	private int totalAmount; 
	
	private String material; 
}
