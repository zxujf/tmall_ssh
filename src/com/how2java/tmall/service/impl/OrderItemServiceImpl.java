package com.how2java.tmall.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.service.OrderItemService;
import com.how2java.tmall.service.ProductImageService;

@Service
public class OrderItemServiceImpl  extends BaseServiceImpl implements OrderItemService {

	
	@Autowired
	ProductImageService productImageService;
	
//	为多个订单计算
	@Override
	public void fill(List<Order> orders) {
		for (Order order : orders) 
			fill(order);
	}
	
//	为订单对象填充其orderItems字段，并且计算出订单总金额，订单总购买数量
	public void fill(Order order) {
			List<OrderItem> orderItems= this.listByParent(order);
			order.setOrderItems(orderItems);
			
			float total = 0;
			int totalNumber = 0;			
			for (OrderItem oi :orderItems) {
				total+=oi.getNumber()*oi.getProduct().getPromotePrice();
				totalNumber+=oi.getNumber();
				
				productImageService.setFirstProdutImage(oi.getProduct());
			}
			order.setTotal(total);
			order.setOrderItems(orderItems);
			order.setTotalNumber(totalNumber);
	}



	
}
