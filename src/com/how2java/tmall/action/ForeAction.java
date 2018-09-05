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
	
//	�ύ����
	@Action("foredoreview")
	public String doreview() {
		t2p(order);
		t2p(product);
		
//		�޸Ķ�������״̬
		order.setStatus(OrderService.finish);
	    
//		��ȡ����content (������Ϣ)
	    String content = review.getContent();
//	       ��������Ϣ����ת��
	    content = HtmlUtils.htmlEscape(content);

	    User user =(User) ActionContext.getContext().getSession().get("user");
	    
//	       Ϊ���۶���review���� ������Ϣ����Ʒ��ʱ�䣬�û�
	    review.setContent(content);
	    review.setProduct(product);
	    review.setCreateDate(new Date());
	    review.setUser(user);
	    
	    reviewService.saveReviewAndUpdateOrderStatus(review,order);
	     
	    showonly = true;
	    return "reviewPage";
	}	
	
//	���۲�Ʒҳ��
	@Action("forereview")
	public String review() {
		t2p(order);
	    orderItemService.fill(order);
//	    ��ȡ��һ���������Ӧ�Ĳ�Ʒ,��Ϊ������ҳ����Ҫ��ʾһ����ƷͼƬ����ô��ʹ�����һ����Ʒ��ͼƬ��
	    product = order.getOrderItems().get(0).getProduct();
//	    ��ȡ�����Ʒ�����ۼ���
	    reviews = reviewService.listByParent(product);
//	    Ϊ��Ʒ������������������
	    productService.setSaleAndReviewNumber(product);
	    return "review.jsp";        
	}
	
	@Action("foredeleteOrder")
	public String deleteOrder(){
		t2p(order);
//		�޸�״̬
	    order.setStatus(OrderService.delete);
	    orderService.update(order);
	    return "success.jsp";		
	}
	
//	ȷ���ջ��ɹ�
	@Action("foreorderConfirmed")
	public String orderConfirmed() {
		t2p(order);
//		�޸Ķ���order��״̬Ϊ�ȴ����ۣ��޸���ȷ��֧��ʱ��
	    order.setStatus(OrderService.waitReview);
	    order.setConfirmDate(new Date());
	    orderService.update(order);
	    return "orderConfirmed.jsp";
	}
	
//	ȷ���ջ�
	@Action("foreconfirmPay")
	public String confirmPay() {
		t2p(order);
	    orderItemService.fill(order);
	    return "confirmPay.jsp";        
	}
	
//	�ҵĶ���ҳ
	@Action("forebought")
	public String bought() {
		User user =(User) ActionContext.getContext().getSession().get("user");
//		��ѯuser���е�״̬����"delete" �Ķ�������os
		orders= orderService.listByUserWithoutDelete(user);
	    orderItemService.fill(orders);
	    return "bought.jsp";        
	}	
	
	@Action("forepayed")
	public String payed() {
	    t2p(order);
//	    �޸Ķ��������״̬��֧��ʱ��
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
//		��session��ȡ�������
	    List<OrderItem> ois= (List<OrderItem>) ActionContext.getContext().getSession().get("orderItems");
//	       �����������ǿգ�����ת����½ҳ��
	    if(ois.isEmpty())
	        return "login.jsp";
	 
//	       ��session�л�ȡuser����
	    User user =(User) ActionContext.getContext().getSession().get("user");
//	      ���ݵ�ǰʱ�����һ��4λ��������ɶ�����
	    String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) +RandomUtils.nextInt(10000);
	    
//	       ��������������������������
	    order.setOrderCode(orderCode);
	    order.setCreateDate(new Date());
	    order.setUser(user);
//	       �Ѷ���״̬����Ϊ�ȴ�֧��
	    order.setStatus(OrderService.waitPay);
	    
	    total = orderService.createOrder(order, ois);
	    return "alipayPage";
	}
	
	@Action("foredeleteOrderItem")
	public String deleteOrderItem(){
	    orderItemService.delete(orderItem);
	    return "success.jsp";
	}
	
//	������������
	@Action("forechangeOrderItem")
	public String changeOrderItem() {
//		��ȡ��ǰ�û�
		User user =(User) ActionContext.getContext().getSession().get("user");
//		�������û���ǰ���е�δ���ɶ�����OrderItem
		List<OrderItem> ois = orderItemService.list("user",user,"order", null);
	    for (OrderItem oi : ois) {
//	    	����product.id�ҵ�ƥ���OrderItem�����޸���������µ����ݿ�
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
	
//	���빺�ﳵ
	@Action("foreaddCart")
	public String addCart() {
        
		User user =(User) ActionContext.getContext().getSession().get("user");
        boolean found = false;
 
        List<OrderItem> ois = orderItemService.list("user",user,"order", null);
//        ����Ѿ����������Ʒ��Ӧ��OrderItem�����һ�û�����ɶ����������ڹ��ﳵ�С� ��ô��Ӧ���ڶ�Ӧ��OrderItem�����ϣ���������
        for (OrderItem oi : ois) {
            if(oi.getProduct().getId()==product.getId()){
                oi.setNumber(oi.getNumber()+num);
                orderItemService.update(oi);
                found = true;
                break;
            }
        }       
//        ��������ڶ�Ӧ��OrderItem,��ô������һ��������OrderItem
        if(!found){
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setNumber(num);
            oi.setProduct(product);
            orderItemService.save(oi);
        }
        return "success.jsp";
    }   	
	
//	 ����
	@Action("forebuy")
	public String buy() {
	    orderItems = new ArrayList<>();
	    for (int oiid : oiids) {
//	    	����ǰ�沽���ȡ��oiids�������ݿ���ȡ��OrderItem���󣬲�����orderItems ������
	        OrderItem oi= (OrderItem) orderItemService.get(oiid);
	        total +=oi.getProduct().getPromotePrice()*oi.getNumber();
	        orderItems.add(oi);
	        productImageService.setFirstProdutImage(oi.getProduct());
	    }
//	       ΪʲôҪ��session? ��Ϊ�������ɶ�����ʱ�򣬻����õ���  
	    ActionContext.getContext().getSession().put("orderItems", orderItems);
	    return "buy.jsp";
	}
	
//	����������
	@Action("forebuyone")
	public String buyone() {
	    User user =(User) ActionContext.getContext().getSession().get("user");
	    boolean found = false;
	    List<OrderItem> ois = orderItemService.list("user",user,"order", null);
	  /*a ����Ѿ����������Ʒ��Ӧ��OrderItem�����һ�û�����ɶ����������ڹ��ﳵ�С� ��ô��Ӧ���ڶ�Ӧ��OrderItem�����ϣ���������
	    a.1 �����û�����user����ѯû�����ɶ����Ķ������
	    a.2 �����������
	    a.3 �����Ʒ��һ���Ļ����ͽ�������׷��
	    a.4 ��ȡ���������� id*/
	    for (OrderItem oi : ois) {
	        if(oi.getProduct().getId()==product.getId()){
	            oi.setNumber(oi.getNumber()+num);
	            orderItemService.update(oi);
	            found = true;
	            oiid = oi.getId();
	            break;
	        }
	    }       
	 
	  /*b ��������ڶ�Ӧ��OrderItem,��ô������һ��������OrderItem
	    b.1 �����µĶ�����
	    b.2 �����������û��Ͳ�Ʒ
	    b.3 ���뵽���ݿ�
	    b.4 ��ȡ���������� id*/
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
	     
/*	    ��ȡ����sort
	    ���sort==null����������
	    ���sort!=null�������sort��ֵ����5��Comparator�Ƚ�����ѡ��һ����Ӧ����������������	 */   
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
	
	//��Ʒҳ
	@Action("foreproduct")
	public String product() {
		t2p(product);
	     
//		��������ͼƬ
		productImageService.setFirstProdutImage(product);
//		���õ���������ͼƬ����
		productSingleImages = productImageService.list("product",product,"type", ProductImageService.type_single);
		productDetailImages = productImageService.list("product",product,"type", ProductImageService.type_detail);
		product.setProductSingleImages(productSingleImages);
	    product.setProductDetailImages(productDetailImages);
	     
//		��ȡ����Ʒ������ֵ����
	    propertyValues = propertyValueService.listByParent(product);       
	 
//		 ��ȡ����Ʒ�����ۼ���
	    reviews = reviewService.listByParent(product);
	    
//	    ����������������������
	    productService.setSaleAndReviewNumber(product);

	    return "product.jsp";        	
	}
//	�˳�
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
	        msg= "�˺��������";
	        return "login.jsp"; 
	    }
	    ActionContext.getContext().getSession().put("user", user_session);
	    return "homePage"; 		
	}
	
	@Action("foreregister")
	public String register() {
//		���˺����������Ž���ת��
		user.setName(HtmlUtils.htmlEscape(user.getName()));
		boolean exist = userService.isExist(user.getName());
//		�ж��û����Ƿ����
		 if(exist){
			 msg = "�û����Ѿ���ʹ��,����ʹ��";
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
