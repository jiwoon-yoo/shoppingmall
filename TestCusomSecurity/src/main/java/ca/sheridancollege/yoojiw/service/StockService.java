package ca.sheridancollege.yoojiw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.sheridancollege.yoojiw.bean.ProudctsWithColorsImages;
import ca.sheridancollege.yoojiw.database.DatabaseAccess;

@Service
public class StockService {

    @Autowired
    private DatabaseAccess da;

    public String adjustCartItemQuantities(int cartId) {
    	
    	String returnVal = ""; 
    	
        List<ProudctsWithColorsImages> items = da.getCartItems(cartId);

        if (items != null) {
            for (ProudctsWithColorsImages i : items) {
                int cartItemQuantity = da.getCartItemsQuantity(i.getCartId(), i.getColorId(), i.getSizeId(), i.getProductId());
                int stockQuantity = da.getStockQuantity(i.getProductId(), i.getColorId(), i.getSizeId());

                if (cartItemQuantity > stockQuantity) {
                    da.updateCartItemsQuantity(i.getCartId(), i.getColorId(), i.getSizeId(), i.getProductId(), stockQuantity);
                    //System.out.println("quantity is modified for product: " + i.getProductName() + " from " + cartItemQuantity + " to " + stockQuantity);
                    returnVal += "quantity is modified for product: " + i.getProductName() + "(" + i.getColorName()  + ")" + " from " + cartItemQuantity + " to " + stockQuantity + "\n"; 
                }
            }
        }
        
        return returnVal; 
    }

    public void addToCart(int cartId, int colorId, int sizeId, int productId) {
        if (da.checkExistingCartItems(cartId, colorId, sizeId, productId)) {
            da.increaseQuantityFromCartItems(cartId, colorId, sizeId, productId);
        } else {
            da.insertOneCartItem(cartId, colorId, sizeId, productId);
        }
    }
}