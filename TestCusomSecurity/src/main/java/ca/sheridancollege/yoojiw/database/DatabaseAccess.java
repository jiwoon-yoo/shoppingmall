package ca.sheridancollege.yoojiw.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import ca.sheridancollege.yoojiw.bean.ProudctsWithColorsImages;
import ca.sheridancollege.yoojiw.bean.User;
import ca.sheridancollege.yoojiw.service.ImageService;

@Repository
public class DatabaseAccess {

	@Autowired
	private NamedParameterJdbcTemplate jdbc; 
	
	@Autowired
	private ImageService imageService;
	
	public User getOneUserByUsername(String username) {
		
		String sql = "SELECT * FROM users WHERE username = :username"; 
		
		MapSqlParameterSource params = new MapSqlParameterSource(); 
		
		params.addValue("username", username);
		
		List<User> rows =  jdbc.query(sql, params, new BeanPropertyRowMapper<User>(User.class)); 
		
		if(rows.size() > 0) {
			
			return rows.get(0); 
		}else {
			
			return null; 
		}
	}
	
	
	public List<String> getAllRolesByUserId(int userId) {
		
		String sql = "SELECT roleName FROM user_role ur INNER JOIN roles r ON ur.roleId = r.roleId WHERE ur.userId = :userId"; 
		
		MapSqlParameterSource params = new MapSqlParameterSource(); 
		
		params.addValue("userId", userId); 
		
		List<Map<String, Object>> rows = jdbc.queryForList(sql, params); 
		List<String> roles = new ArrayList<>(); 
		
		for(Map<String, Object> i : rows) {
			
			roles.add((String)i.get("roleName")); 
		}
		
		return roles; 
	}
	
	
	
	
	//
	public List<ProudctsWithColorsImages> getProudctsWithColorsImagesCategories() {
		
		//image ------ color --- product_color ---- product ----- category 
		String sql = "SELECT p.price, p.productName, pc.productId, pc.colorId, cat.categoryName, minImages.imageUrl\r\n"
				+ "FROM product_color pc\r\n"
				+ "INNER JOIN products p ON pc.productId = p.productId\r\n"
				+ "INNER JOIN colors c ON pc.colorId = c.colorId\r\n"
				+ "INNER JOIN categories cat ON p.categoryId = cat.categoryId\r\n"
				+ "INNER JOIN (\r\n"
				+ "    SELECT productId, colorId, MIN(imageUrl) AS imageUrl\r\n"
				+ "    FROM images\r\n"
				+ "    GROUP BY productId, colorId\r\n"
				+ ") minImages ON pc.productId = minImages.productId AND pc.colorId = minImages.colorId;\r\n"
				+ ""; 
		
		MapSqlParameterSource params = new MapSqlParameterSource(); 
		
		//queryForList 사용하는 법 
		List<ProudctsWithColorsImages> rows = jdbc.query(sql, new BeanPropertyRowMapper<ProudctsWithColorsImages>(ProudctsWithColorsImages.class)); 
	
		return rows;
	}
	
	public List<ProudctsWithColorsImages> getProudctsWithColorsImagesCategoriesByCategoryName(String categoryName) {
		
		String sql = "SELECT p.price, p.productName, pc.productId, pc.colorId, cat.categoryName, minImages.imageUrl\r\n"
				+ "FROM product_color pc\r\n"
				+ "INNER JOIN products p ON pc.productId = p.productId\r\n"
				+ "INNER JOIN colors c ON pc.colorId = c.colorId\r\n"
				+ "INNER JOIN categories cat ON p.categoryId = cat.categoryId\r\n"
				+ "INNER JOIN (\r\n"
				+ "    SELECT productId, colorId, MIN(imageUrl) AS imageUrl\r\n"
				+ "    FROM images\r\n"
				+ "    GROUP BY productId, colorId\r\n"
				+ ") minImages ON pc.productId = minImages.productId AND pc.colorId = minImages.colorId\r\n"
				+ "WHERE cat.categoryName = :categoryName\r\n"
				+ "ORDER BY p.productId;\r\n"
				+ ""; 
		
		MapSqlParameterSource params = new MapSqlParameterSource(); 
		
		params.addValue("categoryName", categoryName); 
		
		//queryForList 사용하는 법 
		List<ProudctsWithColorsImages> rows = jdbc.query(sql, params, new BeanPropertyRowMapper<ProudctsWithColorsImages>(ProudctsWithColorsImages.class)); 

		return rows;
	}
	

	//
	public ProudctsWithColorsImages getOneProduct(int productId) {
		
		String sql = "SELECT * FROM products WHERE productId = :productId"; 
		
		MapSqlParameterSource params = new MapSqlParameterSource(); 
		
		params.addValue("productId", productId); 
		
		List<ProudctsWithColorsImages> rows =  jdbc.query(sql, params, new BeanPropertyRowMapper<ProudctsWithColorsImages>(ProudctsWithColorsImages.class)); 
		
		if(rows.size() > 0 ) {
			
			return rows.get(0);  
		}else {
			
			return null; 
		}
	}
	
	public List<String> getImagesByProductIdAndColorId(int productId, int colorId) {
		
		String sql = "SELECT * FROM images WHERE productId = :productId AND colorId = :colorId";  
				
		MapSqlParameterSource params = new MapSqlParameterSource(); 
		
		params.addValue("productId", productId);
		params.addValue("colorId", colorId);
		
		List<ProudctsWithColorsImages> rows = jdbc.query(sql, params, new BeanPropertyRowMapper<ProudctsWithColorsImages>(ProudctsWithColorsImages.class)); 
				
		List<String> images = new ArrayList<>(); 
		
		for(ProudctsWithColorsImages i : rows) {
			
			images.add(i.getImageUrl()); 
		}

		
		return images; 
	}
	
	public int getStockByProductIdColorIdSizeId(int productId, int colorId, int sizeId) {
		
	     String sql = "SELECT quantity FROM stocks WHERE productId = :productId AND colorId = :colorId AND sizeId = :sizeId";

        // 파라미터 설정
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("productId", productId);
        params.addValue("colorId", colorId);
        params.addValue("sizeId", sizeId);
        
        int quantity =     jdbc.queryForObject(sql, params, Integer.class); 
        
        return quantity; 
	}
	
	public ProudctsWithColorsImages getOneColor(int colorId) {
		
		String sql = "SELECT * FROM colors WHERE colorId = :colorId";
		
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("colorId", colorId);
		
        List<ProudctsWithColorsImages> rows = jdbc.query(sql, params, new BeanPropertyRowMapper<ProudctsWithColorsImages>(ProudctsWithColorsImages.class)); 
		
        if(rows.size() > 0) {
        	return rows.get(0); 
        }else {
        	return null; 
        }
        
	}
	
	
    public List<ProudctsWithColorsImages> getColorsByProduct(int productId) {
        String sql = "SELECT * FROM products p " +
                     "INNER JOIN product_color pc ON p.productId = pc.productId " +
                     "INNER JOIN colors c ON pc.colorId = c.colorId " +
                     "WHERE p.productId = :productId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("productId", productId);

        List<ProudctsWithColorsImages> rows = jdbc.query(sql, params, new BeanPropertyRowMapper<ProudctsWithColorsImages>(ProudctsWithColorsImages.class)); 

        return rows;
    }
    
    
    public int getUserIdByUserName(String username) {
    	
    	String sql = "SELECT userId FROM users WHERE username = :username"; 
    	
    	MapSqlParameterSource params = new MapSqlParameterSource(); 
    	
    	params.addValue("username", username);
    	
    	int userId =  jdbc.queryForObject(sql, params, Integer.class); 
    	
    	return userId;
    }
    
    
    public int getCartIdByUserId(int userId) {
    	
    	String sql = "SELECT cartId FROM carts WHERE userId = :userId";
    	
    	MapSqlParameterSource params = new MapSqlParameterSource(); 
    	
    	params.addValue("userId", userId); 
    	
    	int cartId = jdbc.queryForObject(sql, params, Integer.class);
    	
    	return cartId;
    }

    public void insertOneCartItem(int cartId, int colorId, int sizeId, int productId) {
    	
    	  String sql = "INSERT INTO cartItems(cartId, colorId, sizeId, productId, quantity) VALUES(:cartId, :colorId, :sizeId, :productId, 1)";

          MapSqlParameterSource params = new MapSqlParameterSource();
          params.addValue("cartId", cartId);
          params.addValue("colorId", colorId);
          params.addValue("sizeId", sizeId);
          params.addValue("productId", productId);

          int rowsInserted = jdbc.update(sql, params);

          if (rowsInserted > 0) {
              System.out.println("added");
          } else {
              System.out.println("failt to add");
          }
    }
    
    public void increaseQuantityFromCartItems(int cartId, int colorId, int sizeId, int productId) {
        String sql = "UPDATE cartItems SET quantity = quantity + 1 WHERE cartId = :cartId AND colorId = :colorId AND sizeId = :sizeId AND productId = :productId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("cartId", cartId);
        params.addValue("colorId", colorId);
        params.addValue("sizeId", sizeId);
        params.addValue("productId", productId);

        int rowsUpdated = jdbc.update(sql, params);

        if (rowsUpdated > 0) {
            System.out.println("Added to cart");
        } else {
            System.out.println("Failed to add to cart");
        }
    }
    
    
    
    public List<ProudctsWithColorsImages> getCartItems(int cartId) {
    	
    	String sql = "SELECT ci.*, c.*, p.*, co.*, s.*, minImages.minImageId, minImages.imageUrl\r\n"
    			+ "FROM cartItems ci\r\n"
    			+ "INNER JOIN carts c ON ci.cartId = c.cartId\r\n"
    			+ "INNER JOIN products p ON ci.productId = p.productId\r\n"
    			+ "INNER JOIN colors co ON ci.colorId = co.colorId\r\n"
    			+ "INNER JOIN sizes s ON ci.sizeId = s.sizeId\r\n"
    			+ "INNER JOIN (\r\n"
    			+ "    SELECT MIN(imageId) AS minImageId, productId, colorId, MIN(imageUrl) AS imageUrl\r\n"
    			+ "    FROM images\r\n"
    			+ "    GROUP BY productId, colorId\r\n"
    			+ ") minImages ON ci.productId = minImages.productId AND ci.colorId = minImages.colorId\r\n"
    			+ "WHERE ci.cartId = :cartId;\r\n"
    			+ ""; 


	    MapSqlParameterSource params = new MapSqlParameterSource();
	    params.addValue("cartId", cartId);
	    
	    List<ProudctsWithColorsImages> rows =  jdbc.query(sql, params, new BeanPropertyRowMapper<ProudctsWithColorsImages>(ProudctsWithColorsImages.class)); 
    	
	    if(rows.isEmpty()) {
	    	return null; 		//
	    }else {
    	    return rows;
	    }
	    
    }
    
    
    public void updateCartItemsQuantity(int cartId, int colorId, int sizeId, int productId, int quantity) {
    	String sql = "UPDATE cartItems SET quantity = :quantity WHERE cartId = :cartId AND colorId = :colorId AND sizeId = :sizeId AND productId = :productId";

	    MapSqlParameterSource params = new MapSqlParameterSource()
	        .addValue("quantity", quantity)
	        .addValue("cartId", cartId)
	        .addValue("colorId", colorId)
	        .addValue("sizeId", sizeId)
	        .addValue("productId", productId);

        int rowsUpdated = jdbc.update(sql, params);
        
        if (rowsUpdated > 0) {
            System.out.println("updated");
        } else {
            System.out.println("Fail to update");
        }
    }


    
    public boolean updateStocksQuantity(int productId, int colorId, int sizeId, int deductedQuantity) {
        String sql = "UPDATE stocks SET quantity = quantity - :deductedQuantity WHERE productId = :productId AND colorId = :colorId AND sizeId = :sizeId AND quantity >= :deductedQuantity";

        // 파라미터 설정
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("productId", productId);
        params.addValue("colorId", colorId);
        params.addValue("sizeId", sizeId);
        params.addValue("deductedQuantity", deductedQuantity);

        int rowCount = jdbc.update(sql, params);

        return rowCount > 0;
    }

	
	
	
	public boolean checkExistingCartItems(int cartId, int colorId, int sizeId, int productId) {
	    String sql = "SELECT COUNT(*) FROM cartItems WHERE cartId = :cartId AND colorId = :colorId AND sizeId = :sizeId AND productId = :productId";

	    // 파라미터 설정
	    MapSqlParameterSource params = new MapSqlParameterSource();
	    params.addValue("cartId", cartId);
	    params.addValue("colorId", colorId);
	    params.addValue("sizeId", sizeId);
	    params.addValue("productId", productId);

	    int rowCount = jdbc.queryForObject(sql, params, Integer.class); 
	    
	    if(rowCount == 0) {
	    	return false;
	    }else {
	    	return true; 		//이미 존재할때, true 
	    }
	}
	
	
	public int getCartItemsQuantity(int cartId, int colorId, int sizeId, int productId) {
	    String sql = "SELECT quantity FROM cartItems WHERE cartId = :cartId AND colorId = :colorId AND sizeId = :sizeId AND productId = :productId";
	    
	    MapSqlParameterSource params = new MapSqlParameterSource();
	    params.addValue("cartId", cartId);
	    params.addValue("colorId", colorId);
	    params.addValue("sizeId", sizeId);
	    params.addValue("productId", productId);
	    
	    return jdbc.queryForObject(sql, params, Integer.class);
	}

	public int getStockQuantity(int productId, int colorId, int sizeId) {
	    String sql = "SELECT quantity FROM stocks WHERE productId = :productId AND colorId = :colorId AND sizeId = :sizeId";
	    
	    MapSqlParameterSource params = new MapSqlParameterSource();
	    params.addValue("productId", productId);
	    params.addValue("colorId", colorId);
	    params.addValue("sizeId", sizeId);
	    
	    return jdbc.queryForObject(sql, params, Integer.class);
	}
	
	
	public void insertOneShippings(String recipientName, String postalcode, String street, String apartment, String phone, String shippingMessage, int orderId) {
	    String sql = "INSERT INTO shippings (recipientName, postalcode, street, apartment, phone, shippingMessage, orderId) " +
	                 "VALUES (:recipientName, :postalcode, :street, :apartment, :phone, :shippingMessage, :orderId)";

	    MapSqlParameterSource params = new MapSqlParameterSource()
	        .addValue("recipientName", recipientName)
	        .addValue("postalcode", postalcode)
	        .addValue("street", street)
	        .addValue("apartment", apartment)
	        .addValue("phone", phone)
	        .addValue("shippingMessage", shippingMessage)
	        .addValue("orderId", orderId); 
	        
	    jdbc.update(sql, params);
	}
	
	public void deleteAllCartItemsBycartId(int cartId) {
		
		String sql = "DELETE FROM cartItems WHERE cartId = :cartId"; 
		
		MapSqlParameterSource params = new MapSqlParameterSource(); 
		
		params.addValue("cartId", cartId); 
		
		jdbc.update(sql, params); 
	}
	
	
	public void deleteOneCartItem(int cartId, int colorId, int sizeId, int productId) {
	    String sql = "DELETE FROM cartItems WHERE cartId = :cartId AND colorId = :colorId AND sizeId = :sizeId AND productId = :productId";

	    MapSqlParameterSource params = new MapSqlParameterSource();
	    params.addValue("cartId", cartId);
	    params.addValue("colorId", colorId);
	    params.addValue("sizeId", sizeId);
	    params.addValue("productId", productId);

	    jdbc.update(sql, params);
	}


    public void insertOrder(double totalAmount, int userId) {
        String sql = "INSERT INTO orders (orderDate,  totalAmount, userId) " +
                     "VALUES (NOW(), :totalAmount, :userId)";

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("userId", userId)
            .addValue("totalAmount", totalAmount);

        int rowsInserted = jdbc.update(sql, params);

        if (rowsInserted > 0) {
            // 주문 삽입 성공
            System.out.println("Order inserted successfully.");
        } else {
            // 주문 삽입 실패
            System.out.println("Failed to insert order.");
        }
    }
    
    public void updateOrder(double totalAmount, int orderId) {
        String sql = "UPDATE orders SET totalAmount = :totalAmount WHERE orderId = :orderId";

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("totalAmount", totalAmount)
            .addValue("orderId", orderId);

        jdbc.update(sql, params);
    }

    
    public List<ProudctsWithColorsImages> findAllQuantityPriceByOrderId(int orderId){
    	
    	String sql = "SELECT * FROM orderItems oi INNER JOIN products p ON oi.productId = p.productId WHERE oi.orderId = :orderId"; 
    	
    	MapSqlParameterSource params = new MapSqlParameterSource(); 
    	
    	params.addValue("orderId", orderId);
    	
		List<ProudctsWithColorsImages> rows =  jdbc.query(sql, params, new BeanPropertyRowMapper<ProudctsWithColorsImages>(ProudctsWithColorsImages.class)); 

    	return rows;
    }
    
    ///
    public int getShippingIdByUserId(int userId) {
        String sql = "SELECT shippingId FROM shippings WHERE userId = :userId";
        
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("userId", userId);

        return jdbc.queryForObject(sql, params, Integer.class);
    }
    
    
    public int getCurrentOrderId(int userId) {
        String sql = "SELECT orderId FROM orders " +
                     "WHERE userId = :userId " +
                     "ORDER BY orderDate DESC LIMIT 1";

        MapSqlParameterSource params = new MapSqlParameterSource(); 
           
        params.addValue("userId", userId);

        try {
            int orderId = jdbc.queryForObject(sql, params, Integer.class);
            return orderId;
        } catch (EmptyResultDataAccessException e) {
            // 처리할 예외 처리 (주문이 없을 경우)
            return 0; // 또는 다른 기본값을 사용할 수 있습니다.
        }
    }

    
    public void insertOneOrderItem(int orderId, int productId, int sizeId, int colorId, String shippingStatus) {
        String sql = "INSERT INTO orderItems (orderId, productId, sizeId, colorId, shippingStatus) " +
                     "VALUES (:orderId, :productId, :sizeId, :colorId, :shippingStatus)";
        
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("orderId", orderId)
            .addValue("productId", productId)
            .addValue("sizeId", sizeId)
            .addValue("colorId", colorId)
            .addValue("shippingStatus", shippingStatus); 
            
        jdbc.update(sql, params);
    }


    public void updateCartQuantity(int cartId, int colorId, int sizeId, int productId, int modifiedQuantity) {
        String sql = "UPDATE cartItems SET quantity = :modifiedQuantity WHERE cartId = :cartId AND colorId = :colorId AND sizeId = :sizeId AND productId = :productId";

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("modifiedQuantity", modifiedQuantity)
            .addValue("cartId", cartId)
            .addValue("colorId", colorId)
            .addValue("sizeId", sizeId)
            .addValue("productId", productId);

        jdbc.update(sql, params);
    }
    
    
    public void insertOneUser(String username, String encryptedPassword) {
        String sql = "INSERT INTO users(username, password) VALUES(:username, :password)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", username)
                .addValue("password", encryptedPassword); // 실제로 암호화된 비밀번호를 사용

        jdbc.update(sql, params);
    }
 
    public void insertOneCart(int userId) {
        String sql = "INSERT INTO carts(userId) VALUES(:userId)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId);

        jdbc.update(sql, params);
    }
    
    public void insertUserAdditions(int userId, double totalPayment, int totalPaymentCount) {
        String sql = "INSERT INTO userAdditions (userId, totalPayment, totalPaymentCount, registerDate)\r\n"
        		+ "VALUES (:userId, :totalPayment, :totalPaymentCount, NOW())" ;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("totalPayment", totalPayment)
                .addValue("totalPaymentCount", totalPaymentCount); 

        jdbc.update(sql, params);
    }
    
    public void insertUserRole(int userId, int roleId) {
        String sql = "INSERT INTO user_role (userId, roleId) " +
                     "VALUES (:userId, :roleId)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("roleId", roleId);

        jdbc.update(sql, params);
    }
    
    
    public List<User> getAllUsersUserAdditions() {
    	
    	String sql = "SELECT * FROM users u INNER JOIN userAdditions ua ON u.userId = ua.userId";
    	
    	MapSqlParameterSource params = new MapSqlParameterSource(); 
    	
    	List<User> rows = jdbc.query(sql, params, new BeanPropertyRowMapper<User>(User.class)); 
    	
    	return rows; 
    }

    
    
    public List<User> getAllUsersUserAdditionsByUsername(String username) {
    	
    	String sql = "SELECT * FROM users u INNER JOIN userAdditions ua ON u.userId = ua.userId WHERE u.username = :username";
    	
    	MapSqlParameterSource params = new MapSqlParameterSource(); 
    	params.addValue("username", username); 
    	
    	List<User> rows = jdbc.query(sql, params, new BeanPropertyRowMapper<User>(User.class)); 
    	
    	return rows; 
    }
    
    
    public List<ProudctsWithColorsImages> getAllOrders(){
    	
    	String sql = "SELECT oi.*, s.*, p.*, sz.*, c.*, o.orderDate, minImages.imageUrl\r\n"
    			+ "FROM orderItems oi\r\n"
    			+ "INNER JOIN orders o ON oi.orderId = o.orderId\r\n"
    			+ "INNER JOIN shippings s ON o.orderId = s.orderId\r\n"
    			+ "INNER JOIN products p ON oi.productId = p.productId\r\n"
    			+ "INNER JOIN sizes sz ON oi.sizeId = sz.sizeId\r\n"
    			+ "INNER JOIN colors c ON oi.colorId = c.colorid\r\n"
    			+ "INNER JOIN (\r\n"
    			+ "    SELECT productId, colorId, MIN(imageUrl) AS imageUrl\r\n"
    			+ "    FROM images\r\n"
    			+ "    GROUP BY productId, colorId\r\n"
    			+ ") minImages ON minImages.productId = p.productId AND minImages.colorId = c.colorId"; 
    	
    	MapSqlParameterSource params = new MapSqlParameterSource(); 
    	
		List<ProudctsWithColorsImages> rows =  jdbc.query(sql, params, new BeanPropertyRowMapper<ProudctsWithColorsImages>(ProudctsWithColorsImages.class)); 
    	
    	return rows; 
    }
    
    public List<ProudctsWithColorsImages> getAllOrderItemsByUserId(int userId){
    	
    	String sql = "SELECT oi.*, s.*, p.*, sz.*, c.*, o.orderDate, minImages.imageUrl\r\n"
    			+ "FROM orderItems oi\r\n"
    			+ "INNER JOIN orders o ON oi.orderId = o.orderId\r\n"
    			+ "INNER JOIN shippings s ON o.orderId = s.orderId\r\n"
    			+ "INNER JOIN products p ON oi.productId = p.productId\r\n"
    			+ "INNER JOIN sizes sz ON oi.sizeId = sz.sizeId\r\n"
    			+ "INNER JOIN colors c ON oi.colorId = c.colorid\r\n"
    			+ "INNER JOIN (\r\n"
    			+ "    SELECT productId, colorId, MIN(imageUrl) AS imageUrl\r\n"
    			+ "    FROM images\r\n"
    			+ "    GROUP BY productId, colorId\r\n"
    			+ ") minImages ON minImages.productId = p.productId AND minImages.colorId = c.colorId\r\n"
    			+ "WHERE o.userId = :userId;\r\n"
    			+ ""; 
    	
    	MapSqlParameterSource params = new MapSqlParameterSource(); 
    	params.addValue("userId", userId); 
    	
		List<ProudctsWithColorsImages> rows =  jdbc.query(sql, params, new BeanPropertyRowMapper<ProudctsWithColorsImages>(ProudctsWithColorsImages.class)); 

    	return rows; 
    }
    
    public List<ProudctsWithColorsImages> getAllOrderItemsByOrderId(int orderId){
    	
    	String sql = "SELECT oi.*, s.*, p.*, sz.*, c.*, o.orderDate, minImages.imageUrl\r\n"
    			+ "FROM orderItems oi\r\n"
    			+ "INNER JOIN orders o ON oi.orderId = o.orderId\r\n"
    			+ "INNER JOIN shippings s ON o.orderId = s.orderId\r\n"
    			+ "INNER JOIN products p ON oi.productId = p.productId\r\n"
    			+ "INNER JOIN sizes sz ON oi.sizeId = sz.sizeId\r\n"
    			+ "INNER JOIN colors c ON oi.colorId = c.colorid\r\n"
    			+ "INNER JOIN (\r\n"
    			+ "    SELECT productId, colorId, MIN(imageUrl) AS imageUrl\r\n"
    			+ "    FROM images\r\n"
    			+ "    GROUP BY productId, colorId\r\n"
    			+ ") minImages ON minImages.productId = p.productId AND minImages.colorId = c.colorId\r\n"
    			+ "WHERE o.orderId = :orderId;\r\n"
    			+ ""; 
    	
    	MapSqlParameterSource params = new MapSqlParameterSource(); 
    	params.addValue("orderId", orderId); 
    	
		List<ProudctsWithColorsImages> rows =  jdbc.query(sql, params, new BeanPropertyRowMapper<ProudctsWithColorsImages>(ProudctsWithColorsImages.class)); 

    	return rows; 
    }
    
    
    public List<ProudctsWithColorsImages> getAllOrdersByUserId(int userId) {
    	
    	String sql = "SELECT * FROM orders WHERE userId = :userId"; 
    	
    	MapSqlParameterSource params = new MapSqlParameterSource(); 
    	
    	params.addValue("userId", userId);
    	
		List<ProudctsWithColorsImages> rows =  jdbc.query(sql, params, new BeanPropertyRowMapper<ProudctsWithColorsImages>(ProudctsWithColorsImages.class)); 
  
		return rows; 
    }
    
    
    
    public void updateOneOrderItem(int orderItemId, String newStatus) {
        String sql = "UPDATE orderItems SET shippingStatus = :newStatus WHERE orderItemId = :orderItemId";
        
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("newStatus", newStatus)
            .addValue("orderItemId", orderItemId);
        
        jdbc.update(sql, params);
    }
    
    
    
    public List<ProudctsWithColorsImages> getAllOrdersByRecipientName(String recipientName){
    	
    	String sql = "SELECT oi.*, s.*, p.*, sz.*, c.*,o.orderDate, minImages.imageUrl\r\n"
    			+ "FROM orderItems oi\r\n"
    			+ "INNER JOIN orders o ON oi.orderId = o.orderId\r\n"
    			+ "INNER JOIN shippings s ON o.orderId = s.orderId\r\n"
    			+ "INNER JOIN products p ON oi.productId = p.productId\r\n"
    			+ "INNER JOIN sizes sz ON oi.sizeId = sz.sizeId\r\n"
    			+ "INNER JOIN colors c ON oi.colorId = c.colorid\r\n"
    			+ "INNER JOIN (\r\n"
    			+ "    SELECT productId, colorId, MIN(imageUrl) AS imageUrl\r\n"
    			+ "    FROM images\r\n"
    			+ "    GROUP BY productId, colorId\r\n"
    			+ ") minImages ON minImages.productId = p.productId AND minImages.colorId = c.colorId\r\n"
    			+ "WHERE s.recipientName = :recipientName;\r\n"
    			+ ""; 
    	
    	MapSqlParameterSource params = new MapSqlParameterSource(); 
    	params.addValue("recipientName", recipientName);
    	
		List<ProudctsWithColorsImages> rows =  jdbc.query(sql, params, new BeanPropertyRowMapper<ProudctsWithColorsImages>(ProudctsWithColorsImages.class)); 
    	
    	return rows; 
    }
    public List<ProudctsWithColorsImages> getAllOrdersByRecipientNameShippingStatus(String recipientName, String shippingStatus){
    	
    	String sql = "SELECT oi.*, s.*, p.*, sz.*, c.*, o.orderDate, minImages.imageUrl\r\n"
    			+ "FROM orderItems oi\r\n"
    			+ "INNER JOIN orders o ON oi.orderId = o.orderId\r\n"
    			+ "INNER JOIN shippings s ON o.orderId = s.orderId\r\n"
    			+ "INNER JOIN products p ON oi.productId = p.productId\r\n"
    			+ "INNER JOIN sizes sz ON oi.sizeId = sz.sizeId\r\n"
    			+ "INNER JOIN colors c ON oi.colorId = c.colorid\r\n"
    			+ "INNER JOIN (\r\n"
    			+ "    SELECT productId, colorId, MIN(imageUrl) AS imageUrl\r\n"
    			+ "    FROM images\r\n"
    			+ "    GROUP BY productId, colorId\r\n"
    			+ ") minImages ON minImages.productId = p.productId AND minImages.colorId = c.colorId\r\n"
    			+ "WHERE s.recipientName = :recipientName AND oi.shippingStatus = :shippingStatus;\r\n"
    			+ ""; 
    	
    	MapSqlParameterSource params = new MapSqlParameterSource(); 
    	params.addValue("recipientName", recipientName);
    	params.addValue("shippingStatus", shippingStatus);
    	
		List<ProudctsWithColorsImages> rows =  jdbc.query(sql, params, new BeanPropertyRowMapper<ProudctsWithColorsImages>(ProudctsWithColorsImages.class)); 
    	
    	return rows; 
    }
    
    public void updateUserAdditions(int userId, double totalAmount) {
        String sql = "UPDATE userAdditions " +
                     "SET totalPaymentCount = totalPaymentCount + 1, totalPayment = totalPayment + :totalAmount " +
                     "WHERE userId = :userId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("totalAmount", totalAmount);
        params.addValue("userId", userId);

        jdbc.update(sql, params);
    }

    public List<ProudctsWithColorsImages> getAllColors(){
    	
    	String sql = "SELECT * FROM colors"; 
    	
    	MapSqlParameterSource params = new MapSqlParameterSource(); 
    	
		List<ProudctsWithColorsImages> rows =  jdbc.query(sql, params, new BeanPropertyRowMapper<ProudctsWithColorsImages>(ProudctsWithColorsImages.class)); 
    
		return rows; 
    }
    
    
    
    
    
    
    
    
    
    public void registerNewProduct(int categoryId, String productName, double price, int colorId, int sizeId, String detail, int stockQuantity, MultipartFile[] images, String material) throws IOException {
        //products
    	String insertProductSQL = "INSERT INTO products (productName, price, material, detail, categoryId) " +
                "VALUES (:productName, :price, :material, :detail, :categoryId)";

        MapSqlParameterSource productParams = new MapSqlParameterSource();
        productParams.addValue("productName", productName);
        productParams.addValue("price", price);
        productParams.addValue("material", material);
        productParams.addValue("detail", detail);
        productParams.addValue("categoryId", categoryId); // categoryId를 추가

        KeyHolder productKeyHolder = new GeneratedKeyHolder();
        jdbc.update(insertProductSQL, productParams, productKeyHolder);

        Number generatedKey = productKeyHolder.getKey();
        int productId = generatedKey.intValue();
    	

        //product_color
        String insertColorSizeStockSQL = "INSERT INTO product_color (productId, colorId) " +
                "VALUES (:productId, :colorId)";

        MapSqlParameterSource colorSizeStockParams = new MapSqlParameterSource();
        colorSizeStockParams.addValue("productId", productId);
        colorSizeStockParams.addValue("colorId", colorId);

        jdbc.update(insertColorSizeStockSQL, colorSizeStockParams);

        
        
        // stocks  -------
        String insertStockSQL1 = "INSERT INTO stocks (quantity, productId, colorId, sizeId) " +
                "VALUES (0, :productId, :colorId, 1)";
        String insertStockSQL2 = "INSERT INTO stocks (quantity, productId, colorId, sizeId) " +
                "VALUES (0, :productId, :colorId, 2)";
        String insertStockSQL3 = "INSERT INTO stocks (quantity, productId, colorId, sizeId) " +
                "VALUES (0, :productId, :colorId, 3)";
        
        String updateStockSQL = "UPDATE stocks SET quantity = :quantity WHERE productId = :productId AND colorId = :colorId AND sizeId = :sizeId";

        MapSqlParameterSource stockParams = new MapSqlParameterSource();
        stockParams.addValue("quantity", stockQuantity);
        stockParams.addValue("productId", productId);
        stockParams.addValue("colorId", colorId);
        stockParams.addValue("sizeId", sizeId);

        jdbc.update(insertStockSQL1, stockParams);
        jdbc.update(insertStockSQL2, stockParams);
        jdbc.update(insertStockSQL3, stockParams);
        jdbc.update(updateStockSQL, stockParams); // 실제 사이즈에 대한 데이터를 삽입 또는 업데이트하는 부분



        
       
        // product_size   ---------------
        String insertSizeSQL1 = "INSERT INTO product_size (productId, sizeId) " +
                "VALUES (:productId, 1)";
        String insertSizeSQL2 = "INSERT INTO product_size (productId, sizeId) " +
                "VALUES (:productId, 2)";
        String insertSizeSQL3 = "INSERT INTO product_size (productId, sizeId) " +
                "VALUES (:productId, 3)";

        MapSqlParameterSource sizeParams = new MapSqlParameterSource();
        sizeParams.addValue("productId", productId);

        jdbc.update(insertSizeSQL1, sizeParams);
        jdbc.update(insertSizeSQL2, sizeParams);
        jdbc.update(insertSizeSQL3, sizeParams);
        
        
        
        
        //images
        uploadImages(productId, colorId, images);
    }

    public void registerExistingProduct(int productId, int categoryId, String productName, double price, int colorId, int sizeId, String detail, int stockQuantity, MultipartFile[] images, String material) throws IOException {


        //product_color
        String insertColorSizeStockSQL = "INSERT INTO product_color (productId, colorId) " +
                "VALUES (:productId, :colorId)";

        MapSqlParameterSource colorSizeStockParams = new MapSqlParameterSource();
        colorSizeStockParams.addValue("productId", productId);
        colorSizeStockParams.addValue("colorId", colorId);

        jdbc.update(insertColorSizeStockSQL, colorSizeStockParams);

        
        
        // stocks 
        String insertStockSQL1 = "INSERT INTO stocks (quantity, productId, colorId, sizeId) " +
                "VALUES (0, :productId, :colorId, 1)";
        String insertStockSQL2 = "INSERT INTO stocks (quantity, productId, colorId, sizeId) " +
                "VALUES (0, :productId, :colorId, 2)";
        String insertStockSQL3 = "INSERT INTO stocks (quantity, productId, colorId, sizeId) " +
                "VALUES (0, :productId, :colorId, 3)";
        
        String updateStockSQL = "UPDATE stocks SET quantity = :quantity WHERE productId = :productId AND colorId = :colorId AND sizeId = :sizeId";

        MapSqlParameterSource stockParams = new MapSqlParameterSource();
        stockParams.addValue("quantity", stockQuantity);
        stockParams.addValue("productId", productId);
        stockParams.addValue("colorId", colorId);
        stockParams.addValue("sizeId", sizeId);

        jdbc.update(insertStockSQL1, stockParams);
        jdbc.update(insertStockSQL2, stockParams);
        jdbc.update(insertStockSQL3, stockParams);
        jdbc.update(updateStockSQL, stockParams); // 실제 사이즈에 대한 데이터를 삽입 또는 업데이트하는 부분


        
        //images
        uploadImages(productId, colorId, images);
    }
    
    
    
    public void uploadImages(int productId, int colorId, MultipartFile[] images) throws IOException {
        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                String imageUrl = imageService.saveImage(image); // 이미지 서비스를 통해 이미지를 서버에 저장하고 URL을 반환
                String insertImageSQL = "INSERT INTO images (imageUrl, productId, colorId) " +
                        "VALUES (:imageUrl, :productId, :colorId)";

                MapSqlParameterSource imageParams = new MapSqlParameterSource();
                imageParams.addValue("imageUrl", imageUrl);
                imageParams.addValue("productId", productId);
                imageParams.addValue("colorId", colorId);

                jdbc.update(insertImageSQL, imageParams);
            }
        }
    }
    
    
    
    
    
    
        
    public List<ProudctsWithColorsImages> getAllRegisteredProducts() {
        String sql = "SELECT * \r\n"
        		+ "FROM products p \r\n"
        		+ "INNER JOIN product_color pc ON p.productId = pc.productId\r\n"
        		+ "INNER JOIN product_size ps ON p.productId = ps.productId \r\n"
        		+ "INNER JOIN colors c ON c.colorId = pc.colorId \r\n"
        		+ "INNER JOIN sizes s ON s.sizeId = ps.sizeId\r\n"
        		+ "INNER JOIN stocks st ON st.colorId = c.colorId AND st.productId = p.productId AND st.sizeId = s.sizeId \r\n"
        		+ "INNER JOIN categories cg ON p.categoryId = cg.categoryId\r\n"
        		+ "ORDER BY p.productId";

        MapSqlParameterSource params = new MapSqlParameterSource();
  
        List<ProudctsWithColorsImages> rows = jdbc.query(sql, params, new BeanPropertyRowMapper<>(ProudctsWithColorsImages.class));

        return rows;
    }

    public void modifyQuantity(int stockId, int newQuantity) {
        String sql = "UPDATE stocks SET quantity = :newQuantity WHERE stockId = :stockId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("stockId", stockId);
        params.addValue("newQuantity", newQuantity);

        int rowsUpdated = jdbc.update(sql, params);

        if (rowsUpdated > 0) {
            System.out.println("Stock quantity updated successfully");
        } else {
            System.out.println("Failed to update stock quantity");
        }
    }





    
}
