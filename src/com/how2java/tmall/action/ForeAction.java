package com.how2java.tmall.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.xwork.math.RandomUtils;
import org.apache.struts2.convention.annotation.Action;
import org.springframework.web.util.HtmlUtils;

import com.how2java.tmall.comparator.ProductAllComparator;
import com.how2java.tmall.comparator.ProductDateComparator;
import com.how2java.tmall.comparator.ProductPriceComparator;
import com.how2java.tmall.comparator.ProductReviewComparator;
import com.how2java.tmall.comparator.ProductSaleCountComparator;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.User;
import com.how2java.tmall.service.OrderService;
import com.how2java.tmall.service.ProductImageService;
import com.opensymphony.xwork2.ActionContext;

public class ForeAction extends Action4Result {
	
//	提交评价
	@Action("foredoreview")
	public String doreview() {
		t2p(order);
		t2p(product);
		
//		修改订单对象状态
		order.setStatus(OrderService.finish);
	    
//		获取参数content (评价信息)
	    String content = review.getContent();
//	       对评价信息进行转义
	    content = HtmlUtils.htmlEscape(content);

	    User user =(User) ActionContext.getContext().getSession().get("user");
	    
//	       为评价对象review设置 评价信息，产品，时间，用户
	    review.setContent(content);
	    review.setProduct(product);
	    review.setCreateDate(new Date());
	    review.setUser(user);
	    
	    reviewService.saveReviewAndUpdateOrderStatus(review,order);
	     
	    showonly = true;
	    return "reviewPage";
	}	
	
//	评价产品页面
	@Action("forereview")
	public String review() {
		t2p(order);
	    orderItemService.fill(order);
//	    获取第一个订单项对应的产品,因为在评价页面需要显示一个产品图片，那么就使用这第一个产品的图片了
	    product = order.getOrderItems().get(0).getProduct();
//	    获取这个产品的评价集合
	    reviews = reviewService.listByParent(product);
//	    为产品设置评价数量和销量
	    productService.setSaleAndReviewNumber(product);
	    return "review.jsp";        
	}
	
	@Action("foredeleteOrder")
	public String deleteOrder(){
		t2p(order);
//		修改状态
	    order.setStatus(OrderService.delete);
	    orderService.update(order);
	    return "success.jsp";		
	}
	
//	确认收货成功
	@Action("foreorderConfirmed")
	public String orderConfirmed() {
		t2p(order);
//		修改对象order的状态为等待评价，修改其确认支付时间
	    order.setStatus(OrderService.waitReview);
	    order.setConfirmDate(new Date());
	    orderService.update(order);
	    return "orderConfirmed.jsp";
	}
	
//	确认收货
	@Action("foreconfirmPay")
	public String confirmPay() {
		t2p(order);
	    orderItemService.fill(order);
	    return "confirmPay.jsp";        
	}
	
//	我的订单页
	@Action("forebought")
	public String bought() {
		User user =(User) ActionContext.getContext().getSession().get("user");
//		查询user所有的状态不是"delete" 的订单集合os
		orders= orderService.listByUserWithoutDelete(user);
	    orderItemService.fill(orders);
	    return "bought.jsp";        
	}	
	
	@Action("forepayed")
	public String payed() {
	    t2p(order);
//	    修改订单对象的状态和支付时间
	    order.setStatus(OrderService.waitDelivery);
	    order.setPayDate(new Date());
	    orderService.update(order);
	    return "payed.jsp";     
	}
	
	@Action("forealipay")
	public String forealipay(){
		return "alipay.jsp";
	}
	
	@Action("forecreateOrder")
	public String createOrder(){
//		从session获取订单项集合
	    List<OrderItem> ois= (List<OrderItem>) ActionContext.getContext().getSession().get("orderItems");
//	       如果订单项集合是空，则跳转到登陆页面
	    if(ois.isEmpty())
	        return "login.jsp";
	 
//	       从session中获取user对象
	    User user =(User) ActionContext.getContext().getSession().get("user");
//	      根据当前时间加上一个4位随机数生成订单号
	    String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) +RandomUtils.nextInt(10000);
	    
//	       根据上述参数，创建订单对象
	    order.setOrderCode(orderCode);
	    order.setCreateDate(new Date());
	    order.setUser(user);
//	       把订单状态设置为等待支付
	    order.setStatus(OrderService.waitPay);
	    
	    total = orderService.createOrder(order, ois);
	    return "alipayPage";
	}
	
	@Action("foredeleteOrderItem")
	public String deleteOrderItem(){
	    orderItemService.delete(orderItem);
	    return "success.jsp";
	}
	
//	调整订单数量
	@Action("forechangeOrderItem")
	public String changeOrderItem() {
//		获取当前用户
		User user =(User) ActionContext.getContext().getSession().get("user");
//		遍历出用户当前所有的未生成订单的OrderItem
		List<OrderItem> ois = orderItemService.list("user",user,"order", null);
	    for (OrderItem oi : ois) {
//	    	根据product.id找到匹配的OrderItem，并修改数量后更新到数据库
	        if(oi.getProduct().getId()==product.getId()){
	            oi.setNumber(num);
	            orderItemService.update(oi);
	            break;
	        }
	         
	    }       
	    return "success.jsp";
	}	
	
	@Action("forecart")
	public String cart() {
		User user =(User) ActionContext.getContext().getSession().get("user");
	    orderItems = orderItemService.list("user",user,"order", null);
	    for (OrderItem orderItem : orderItems) 
			productImageService.setFirstProdutImage(orderItem.getProduct());
	    return "cart.jsp";
	} 
	
//	加入购物车
	@Action("foreaddCart")
	public String addCart() {
        
		User user =(User) ActionContext.getContext().getSession().get("user");
        boolean found = false;
 
        List<OrderItem> ois = orderItemService.list("user",user,"order", null);
//        如果已经存在这个产品对应的OrderItem，并且还没有生成订单，即还在购物车中。 那么就应该在对应的OrderItem基础上，调整数量
        for (OrderItem oi : ois) {
            if(oi.getProduct().getId()==product.getId()){
                oi.setNumber(oi.getNumber()+num);
                orderItemService.update(oi);
                found = true;
                break;
            }
        }       
//        如果不存在对应的OrderItem,那么就新增一个订单项OrderItem
        if(!found){
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setNumber(num);
            oi.setProduct(product);
            orderItemService.save(oi);
        }
        return "success.jsp";
    }   	
	
//	 结算
	@Action("forebuy")
	public String buy() {
	    orderItems = new ArrayList<>();
	    for (int oiid : oiids) {
//	    	根据前面步骤获取的oiids，从数据库中取出OrderItem对象，并放入orderItems 集合中
	        OrderItem oi= (OrderItem) orderItemService.get(oiid);
	        total +=oi.getProduct().getPromotePrice()*oi.getNumber();
	        orderItems.add(oi);
	        productImageService.setFirstProdutImage(oi.getProduct());
	    }
//	       为什么要用session? 因为后续生成订单的时候，还会用到它  
	    ActionContext.getContext().getSession().put("orderItems", orderItems);
	    return "buy.jsp";
	}
	
//	新增订单项
	@Action("forebuyone")
	public String buyone() {
	    User user =(User) ActionContext.getContext().getSession().get("user");
	    boolean found = false;
	    List<OrderItem> ois = orderItemService.list("user",user,"order", null);
	  /*a 如果已经存在这个产品对应的OrderItem，并且还没有生成订单，即还在购物车中。 那么就应该在对应的OrderItem基础上，调整数量
	    a.1 基于用户对象user，查询没有生成订单的订单项集合
	    a.2 遍历这个集合
	    a.3 如果产品是一样的话，就进行数量追加
	    a.4 获取这个订单项的 id*/
	    for (OrderItem oi : ois) {
	        if(oi.getProduct().getId()==product.getId()){
	            oi.setNumber(oi.getNumber()+num);
	            orderItemService.update(oi);
	            found = true;
	            oiid = oi.getId();
	            break;
	        }
	    }       
	 
	  /*b 如果不存在对应的OrderItem,那么就新增一个订单项OrderItem
	    b.1 生成新的订单项
	    b.2 设置数量，用户和产品
	    b.3 插入到数据库
	    b.4 获取这个订单项的 id*/
	    if(!found){
	        OrderItem oi = new OrderItem();
	        oi.setUser(user);
	        oi.setNumber(num);
	        oi.setProduct(product);
	        orderItemService.save(oi);
	        oiid = oi.getId();
	    }
	    return "buyPage";
	}
	
	@Action("foresearch")
	public String search(){
	    products= productService.search(keyword,0,20);
	    productService.setSaleAndReviewNumber(products);
	    for (Product product : products) 
	    	productImageService.setFirstProdutImage(product);	
		
	    
		return "searchResult.jsp";
	}	
	
	@Action("forecategory")
	public String category(){
	    t2p(category);
	    productService.fill(category);
	    productService.setSaleAndReviewNumber(category.getProducts());       
	     
/*	    获取参数sort
	    如果sort==null，即不排序
	    如果sort!=null，则根据sort的值，从5个Comparator比较器中选择一个对应的排序器进行排序	 */   
	    if(null!=sort){
	    switch(sort){
	        case "review":
	            Collections.sort(category.getProducts(),new ProductReviewComparator());
	            break;
	        case "date" :
	            Collections.sort(category.getProducts(),new ProductDateComparator());
	            break;
	             
	        case "saleCount" :
	            Collections.sort(category.getProducts(),new ProductSaleCountComparator());
	            break;
	             
	        case "price":
	            Collections.sort(category.getProducts(),new ProductPriceComparator());
	            break;
	             
	        case "all":
	            Collections.sort(category.getProducts(),new ProductAllComparator());
	            break;
	        }
	    }
	    return "category.jsp";  		
	}
	
	@Action("foreloginAjax")
	public String loginAjax() {
		
		user.setName(HtmlUtils.htmlEscape(user.getName()));
	    User user_session = userService.get(user.getName(),user.getPassword());
	      
	    if(null==user_session)
	        return "fail.jsp"; 
	    
	    ActionContext.getContext().getSession().put("user", user_session);
	    return "success.jsp"; 		
	}	
	
	@Action("forecheckLogin")
	public String checkLogin() {
		User u =(User) ActionContext.getContext().getSession().get("user");
		if(null==u)
			return "fail.jsp";
		else
			return "success.jsp";
	}
	
	//产品页
	@Action("foreproduct")
	public String product() {
		t2p(product);
	     
//		设置首张图片
		productImageService.setFirstProdutImage(product);
//		设置单个和详情图片集合
		productSingleImages = productImageService.list("product",product,"type", ProductImageService.type_single);
		productDetailImages = productImageService.list("product",product,"type", ProductImageService.type_detail);
		product.setProductSingleImages(productSingleImages);
	    product.setProductDetailImages(productDetailImages);
	     
//		获取本产品的属性值集合
	    propertyValues = propertyValueService.listByParent(product);       
	 
//		 获取本产品的评价集合
	    reviews = reviewService.listByParent(product);
	    
//	    设置销售数量和评价数量
	    productService.setSaleAndReviewNumber(product);

	    return "product.jsp";        	
	}
//	退出
	@Action("forelogout")
	public String logout() {
		ActionContext.getContext().getSession().remove("user");
		return "homePage"; 	
	}
	
	@Action("forelogin")
	public String login() {
		user.setName(HtmlUtils.htmlEscape(user.getName()));
	    User user_session = userService.get(user.getName(),user.getPassword());
	    if(null==user_session){
	        msg= "账号密码错误";
	        return "login.jsp"; 
	    }
	    ActionContext.getContext().getSession().put("user", user_session);
	    return "homePage"; 		
	}
	
	@Action("foreregister")
	public String register() {
//		把账号里的特殊符号进行转义
		user.setName(HtmlUtils.htmlEscape(user.getName()));
		boolean exist = userService.isExist(user.getName());
//		判断用户名是否存在
		 if(exist){
			 msg = "用户名已经被使用,不能使用";
			 return "register.jsp";  
		 }
		 userService.save(user);
		 return "registerSuccessPage";
	}	
	@Action("forehome")
	public String home() {
		categorys = categoryService.list();
		productService.fill(categorys);
		productService.fillByRow(categorys);
		return "home.jsp";
	}
}
