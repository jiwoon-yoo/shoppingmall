package ca.sheridancollege.yoojiw.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.sheridancollege.yoojiw.bean.OrderForm;
import ca.sheridancollege.yoojiw.bean.ProudctsWithColorsImages;
import ca.sheridancollege.yoojiw.bean.Status;
import ca.sheridancollege.yoojiw.database.DatabaseAccess;
import ca.sheridancollege.yoojiw.service.ImageService;
import ca.sheridancollege.yoojiw.service.OrderService;
import ca.sheridancollege.yoojiw.service.StockService;
import jakarta.servlet.http.HttpSession;



@Controller
public class MainController {

	@Autowired
	private DatabaseAccess da;

    @Autowired
    private StockService stockService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder; 
    
    @Autowired
    private OrderService orderService;

	@Autowired
	private ImageService imageService; 
	
    
    
	
	@GetMapping("/")
	public String index() {
		
		return "index"; 
	}
	
	@GetMapping("/login")
	public String login() {
		
		return "login"; 
	}
	
	@GetMapping("/secure/secureIndex")
	public String secureIndex() {
		
		return "secure/secureIndex"; 
	}
	

	//header footer 
	@GetMapping("/fragments/header")
	public String header() {
		
		return "fragments/header"; 
	}
	@GetMapping("/fragments/footer")
	public String footer() {
		
		return "fragments/footer"; 
	}
	
	
	@GetMapping("/register")
	public String register() {
		
		return "register"; 
	}
	

	@PostMapping("/register")
	public String postRegister(@RequestParam String username, @RequestParam String password) {
		
		//users에 insert 
		String encryptedPassword = bCryptPasswordEncoder.encode(password); 
		da.insertOneUser(username, encryptedPassword); 
		int userId = da.getUserIdByUserName(username); 
		da.insertUserRole(userId  , 2);		//by default, user account  
		
		
		
		//cart 만들기 
		da.insertOneCart(userId); 
		
		//userAdditions
		da.insertUserAdditions(userId, 0, 0); 		////
		
		
		return "redirect:/"; 
	}
	
	@GetMapping("/permission-denied")			
	public String permissionDenied() {
		
		return "error/permission-denied"; 
	}
	
	@GetMapping("/error")	//////주의!!!! Spring은 "/error" 페이지를 찾지 못하면 기본적으로 "status code 999"와 함께 에러를 표시하려고 합니다.
	public String error() {
		
		return "redirect:/"; 
	}
	
	
	
	//
	@GetMapping("/view/all")
	public String allProducts(Model model) {
		
		model.addAttribute("productData", 	da.getProudctsWithColorsImagesCategories());
		
		return "viewAll"; 
	}
	@GetMapping("/view/top")
	public String showTop(Model model) {

		
		model.addAttribute("cat", "top");
		model.addAttribute("productData", da.getProudctsWithColorsImagesCategoriesByCategoryName("top")); 		/////
		
		return "view";
	}

	@GetMapping("/view/bottom")
	public String showBottom(Model model) {

		model.addAttribute("cat", "bottom"); 
		model.addAttribute("productData", da.getProudctsWithColorsImagesCategoriesByCategoryName("bottom")); 

		return "view";
	}

	@GetMapping("/view/dress")
	public String showDress(Model model) {

		model.addAttribute("cat", "dress"); 
		model.addAttribute("productData", da.getProudctsWithColorsImagesCategoriesByCategoryName("dress")); 

		
		return "view";
	}
	@GetMapping("/view/jacketcoat")
	public String showJacketcoat(Model model) {

		model.addAttribute("cat", "jacketcoat"); 
		model.addAttribute("productData", da.getProudctsWithColorsImagesCategoriesByCategoryName("jacketcoat")); 
		
		return "view";
	}
	
	
	
	
	
	
	
	
	//
	@GetMapping("/showProductDetail/{productId}/{colorId}")
	public String showProductDetail(Model model, @PathVariable("productId") int productId, @PathVariable("colorId") int colorId) {
		
    	model.addAttribute("productId", productId);     	
    	model.addAttribute("colorId", colorId); 
		
		model.addAttribute("product", da.getOneProduct(productId)); 
		model.addAttribute("color", da.getOneColor(colorId)); 
		model.addAttribute("colors",da.getColorsByProduct(productId)); 
		model.addAttribute("images", da.getImagesByProductIdAndColorId(productId, colorId) ); 
		model.addAttribute("quantityS", da.getStockByProductIdColorIdSizeId(productId, colorId, 1)); 
		model.addAttribute("quantityM", da.getStockByProductIdColorIdSizeId(productId, colorId, 2)); 
		model.addAttribute("quantityL", da.getStockByProductIdColorIdSizeId(productId, colorId, 3)); 
				
		return "productDetail"; 
	}
	
	
	
	
	
	
	
	//
	@GetMapping("/myaccount")
	public String myaccount() {
		
		return "secure/myaccount"; 
	}
	
	//
	@GetMapping("/cart")
	public String cart(Model model, @ModelAttribute("returnVal") String returnVal) {
		
		//username -> useId -> "cartId"
	   	org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	   	String username = authentication.getName();
	   	
		//get userId by username 
		int userId = da.getUserIdByUserName(username); 
		int cartId = da.getCartIdByUserId(userId); 


	    // 장바구니 항목 가져오기
	    List<ProudctsWithColorsImages> cartItems = da.getCartItems(cartId);

	    model.addAttribute("cartItems", cartItems);
		model.addAttribute("returnVal", returnVal); 
		
		
		return "secure/cart"; 
	}


    @GetMapping("/removeItem/{cartId}/{colorId}/{sizeId}/{productId}")
    public String removeItem(Model model, @PathVariable int cartId, @PathVariable int colorId, @PathVariable int sizeId, @PathVariable int productId) {
       
    	da.deleteOneCartItem(cartId, colorId, sizeId, productId); 
    
    	return "redirect:/cart"; 
    }

	
	@PostMapping("/addToCart")
	public String addToCart(HttpSession session, @RequestParam int productId, @RequestParam int colorId, @RequestParam("sizeSelect") int sizeId) {
		
		//username -> useId -> "cartId"
	   	org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	   	String username = authentication.getName();
	   	
		//get userId by username 
		int userId = da.getUserIdByUserName(username); 
		int cartId = da.getCartIdByUserId(userId); 

		stockService.addToCart(cartId, colorId, sizeId, productId);		
		
		return "redirect:/cart"; 
	}
	
	
	//for footer page
	@GetMapping("/extraDetail/{detail}")
	public String extraDetail(@PathVariable String detail, Model model) {
		
		model.addAttribute("detail", detail); 
		
		return "extraDetail"; 
	}
	
	
	
	//
	@GetMapping("/checkout")
	public String checkout(Model model, RedirectAttributes redirectAttributes) {
		
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication(); 
		String username = auth.getName(); 
		int userId = da.getUserIdByUserName(username); 
		int cartId = da.getCartIdByUserId(userId); 
		
		 // 장바구니 항목 수량 조정  -----> 
	    String returnVal = stockService.adjustCartItemQuantities(cartId);

	    if(returnVal != "") {
	    	
	    	redirectAttributes.addFlashAttribute("returnVal", returnVal);  
	    	
	    	return "redirect:/cart"; 
	    	
	    }else {
	    
		    model.addAttribute("orderForm", new OrderForm());
		    model.addAttribute("statuses", Status.values());		
				
			return "secure/checkout"; 
	    }
	    
	}
	
	
	@GetMapping("/orderSubmit")
	public String orderResult() {
		
		
		
		return "secure/orderSubmit";  
	}
	
	
	//
    @PostMapping("/submitOrder")
    public String submitOrder(Model model, @ModelAttribute OrderForm orderForm) {
        org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        int orderId = orderService.submitOrder(orderForm, username);
        
        if(orderId > 0) {
        	
        	return "redirect:/orderDetail/" + orderId; 
        }else {	//payment fail
      
        	  return "secure/failOrder";
        }
        
      
    }


	@GetMapping("/cartQuantityModify")
	public String cartQuantityModify(@RequestParam int cartId, @RequestParam int colorId, @RequestParam int sizeId, @RequestParam int productId,  @RequestParam int modifiedQuantity) {
		
		da.updateCartQuantity(cartId, colorId, sizeId, productId, modifiedQuantity); 
		
		return "redirect:/cart"; 
	}
	
	
	
	
	
	@GetMapping("/secure/admin/customer1")
	public String customer1(Model model) {
		
		model.addAttribute("users", da.getAllUsersUserAdditions()); 
		
		return "secure/admin/customer1"; 
	}
	
	
	@GetMapping("/secure/admin/customer2")
	public String customer2() {		//user detail
		
		return "secure/admin/customer2"; 
	}
	
	
	@GetMapping("/secure/admin/product2")
	public String product2(Model model) {		//product registeration
		
		//color list 
		model.addAttribute("colors", da.getAllColors()); 
		
		return "secure/admin/product2"; 
	}
	
	@PostMapping("/productRegister")
	public String productRegister(@RequestParam("category") int categoryId,
	                              @RequestParam("productName") String productName,
	                              @RequestParam("price") double price,
	                              @RequestParam("color") int colorId,
	                              @RequestParam("size") int sizeId,
	                              @RequestParam("detail") String detail,
	                              @RequestParam("stockQuantity") int stockQuantity,
	                              @RequestParam("images") MultipartFile[] images, 
	                              @RequestParam("material") String material) throws IOException {

		//register "new" product
	    da.registerNewProduct(categoryId, productName, price, colorId, sizeId, detail, stockQuantity, images, material);
	    //productId = 0  for now 
	    return "redirect:/"; // 제품 목록 페이지로 리다이렉트
	}

	
	
	
	
	@GetMapping("/secure/admin/product1")
	public String product1(Model model) {
		
		model.addAttribute("orders", da.getAllOrders()); 			
		
		Status[] statusArray = Status.values();
	    String[] statusStrings = new String[statusArray.length];
	    for (int i = 0; i < statusArray.length; i++) {
	        statusStrings[i] = statusArray[i].toString();
	    }
	    
	    model.addAttribute("statuses", statusStrings);
		
		return "secure/admin/product1"; 
	}
	
	
	@GetMapping("/secure/admin/product3")
	public String product3(Model model) {	
		
		model.addAttribute("products", da.getAllRegisteredProducts()); 
		
		return "secure/admin/product3"; 
	}
	
	@PostMapping("/updateStatus")
	public String updateStatus(@RequestParam int orderItemId, @RequestParam String newStatus) {
		
		da.updateOneOrderItem(orderItemId, newStatus); 
		
		return "redirect:/secure/admin/product1"; 
	}
	
	@GetMapping("/search")
	public String search(Model model, @RequestParam String recipientName, @RequestParam String shippingStatus) {
		  
		if(shippingStatus.equals("ALL")) {
			
			model.addAttribute("orders", da.getAllOrdersByRecipientName(recipientName)); 
		}else {
			
			model.addAttribute("orders", da.getAllOrdersByRecipientNameShippingStatus(recipientName, shippingStatus)); 
		}
		
		
		
		Status[] statusArray = Status.values();
	    String[] statusStrings = new String[statusArray.length];
	    for (int i = 0; i < statusArray.length; i++) {
	        statusStrings[i] = statusArray[i].toString();
	    }
	    
	    model.addAttribute("statuses", statusStrings);
		
		return "secure/admin/product1"; 
	}
	
	@PostMapping("/searchByUsername")
	public String searchByUsername(Model model, @RequestParam String username) {
		
		model.addAttribute("users", da.getAllUsersUserAdditionsByUsername(username)); 

		
		return "secure/admin/customer1"; 
	}
	
	@GetMapping("/clearUsernameSearch")
	public String clearUsernameSearch(Model model) {
		
		return "redirect:/secure/admin/customer1"; 
	}
	
	
	@GetMapping("/userDetail/{userId}")
	public String userDetail(Model model, @PathVariable int userId) {
		
		
		model.addAttribute("orderItems", da.getAllOrderItemsByUserId(userId)); 
		
		Status[] statusArray = Status.values();
	    String[] statusStrings = new String[statusArray.length];
	    for (int i = 0; i < statusArray.length; i++) {
	        statusStrings[i] = statusArray[i].toString();
	    }
	    
	    model.addAttribute("statuses", statusStrings);

		
		return "secure/admin/customer2"; 
	}

	
	@GetMapping("/addModel/{productId}/{categoryId}/{productName}/{price}/{detail}/{material}")
	public String addModel(Model model,
	                       @PathVariable int productId,
	                       @PathVariable int categoryId,
	                       @PathVariable String productName,
	                       @PathVariable double price,
	                       @PathVariable String detail,
	                       @PathVariable String material) {
		
		ProudctsWithColorsImages product = new ProudctsWithColorsImages(); 
		product.setProductId(productId); 
		product.setCategoryId(categoryId); 
		product.setProductName(productName); 
		product.setPrice(price); 
		product.setDetail(detail); 
		product.setMaterial(material); 
		
		//color list 
		model.addAttribute("colors", da.getAllColors()); 
		
		//2 way bidning 
		model.addAttribute("product", product); 
		
		
	    return "secure/admin/product4";
	}

	@PostMapping("/registerModel")
	public String registerModel(@ModelAttribute ProudctsWithColorsImages product, @RequestParam("images") MultipartFile[] images) throws IOException {
		
	    da.registerExistingProduct(product.getProductId(), product.getCategoryId(), product.getProductName(), product.getPrice(), product.getColorId(), product.getSizeId(), product.getDetail(), product.getQuantity(), images, product.getMaterial());

		
		return "redirect:/secure/admin/product3"; 
	}

	@PostMapping("/modfiyQuantity")
	public String modfiyQuantity(@RequestParam int stockId, @RequestParam int quantity){
		
		da.modifyQuantity(stockId, quantity); 
		
		return "redirect:/secure/admin/product3"; 
	}
	
	@GetMapping("/mypage")
	public String mypage(Model model) {
		
		//username -> useId -> "cartId"
	   	org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	   	String username = authentication.getName();
	   	
		//get userId by username 
		int userId = da.getUserIdByUserName(username); 
		
		model.addAttribute("orders", da.getAllOrdersByUserId(userId)); 
		

		
		return "secure/mypage"; 
	}

	@GetMapping("/orderDetail/{orderId}")
	public String orderDetail(Model model, @PathVariable int orderId) {
		
		model.addAttribute("orderItems", da.getAllOrderItemsByOrderId(orderId)); 
		
		return "secure/orderDetail"; 
	}
	
	@GetMapping("/requestReturn/{orderItemId}/{orderId}")
	public String requestReturn(@PathVariable int orderItemId, @PathVariable int orderId){
		
		da.updateOneOrderItem(orderItemId, Status.RETURNING.toString());
		
		return "redirect:/orderDetail/" + orderId; 
	}
	
	
}
