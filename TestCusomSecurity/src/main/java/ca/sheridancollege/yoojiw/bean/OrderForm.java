package ca.sheridancollege.yoojiw.bean;

import lombok.Data;

@Data
public class OrderForm {
    private int shippingId;		//
    private String recipientName;
    private String postalcode;
    private String street;
    private String apartment;
    private String phone;		
    private String shippingMessage;
    
    private String cardNumber;
    private String expDate;
    private String securityCode;
}



