package ca.sheridancollege.yoojiw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.sheridancollege.yoojiw.bean.OrderForm;
import ca.sheridancollege.yoojiw.bean.ProudctsWithColorsImages;
import ca.sheridancollege.yoojiw.bean.Status;
import ca.sheridancollege.yoojiw.database.DatabaseAccess;

@Service
public class OrderService {

    @Autowired
    private DatabaseAccess da;

    public int submitOrder(OrderForm orderForm, String username) {
        int userId = da.getUserIdByUserName(username);
        int cartId = da.getCartIdByUserId(userId);
        
        // OrderForm에서 데이터 가져오기
        String cardNumber = orderForm.getCardNumber();
        String expDate = orderForm.getExpDate();
        String securityCode = orderForm.getSecurityCode();

        if (cardNumber.equals("1234") && expDate.equals("1234") && securityCode.equals("1234")) {
            // 결제 처리 및 주문 생성 로직을 여기에 구현
            
            // shipping table 생성
            da.insertOrder(0, userId); // total amount - 0 for now
            int currentOrderId = da.getCurrentOrderId(userId);
            da.insertOneShippings(orderForm.getRecipientName(), orderForm.getPostalcode(), orderForm.getStreet(), orderForm.getApartment(), orderForm.getPhone(), orderForm.getShippingMessage(), currentOrderId);
            
            List<ProudctsWithColorsImages> carts = da.getCartItems(cartId);
            
            for (ProudctsWithColorsImages i : carts) {
                for (int j = 0; j < i.getQuantity(); j++) {
                    da.insertOneOrderItem(currentOrderId, i.getProductId(), i.getSizeId(), i.getColorId(), Status.ORDERED.toString());
                }
            }
            
            // orders table - totalAmount 계산 및 업데이트
            List<ProudctsWithColorsImages> rows = da.findAllQuantityPriceByOrderId(currentOrderId);
            double totalAmount = 0;
            for (ProudctsWithColorsImages i : rows) {
                totalAmount += i.getPrice();
            }

            da.updateOrder(totalAmount, currentOrderId);

            //userAdditions table update 
            da.updateUserAdditions(userId, totalAmount);
            
            
            // stock 수량 감소
            for (ProudctsWithColorsImages i : carts) {
                da.updateStocksQuantity(i.getProductId(), i.getColorId(), i.getSizeId(), i.getQuantity()); // getQuantity - deductedQuantity
            }
            
            // 장바구니 비우기
            da.deleteAllCartItemsBycartId(cartId);
            System.out.println("currentOrder id : " + currentOrderId);
            return currentOrderId;  
        } else {
            // 결제 실패 처리
        	return -1; 
        }
    }
	
	
}
