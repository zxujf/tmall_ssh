package com.how2java.tmall.service.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.User;
import com.how2java.tmall.service.OrderItemService;
import com.how2java.tmall.service.OrderService;

@Service
public class OrderServiceImpl  extends BaseServiceImpl implements OrderService {

	@Autowired OrderItemService orderItemService;
	
//	事务
	@Transactional(propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
	public float createOrder(Order order, List<OrderItem> ois) {
//		把订单插入到数据库中
		save(order);
		float total =0;
//		为每个OrderItem设置其订单
		for (OrderItem oi: ois) {
	        oi.setOrder(order);
	        orderItemService.update(oi);
//	        累计金额并返回
	        total+=oi.getProduct().getPromotePrice()*oi.getNumber();
	    }
		return total;
	}

//	查询user所有的状态不是"delete" 的订单集合os
	@Override
	public List<Order> listByUserWithoutDelete(User user){
		DetachedCriteria dc = DetachedCriteria.forClass(clazz);
		dc.add(Restrictions.eq("user", user));
//		status不等于delete
		dc.add(Restrictions.ne("status", OrderService.delete));
		return findByCriteria(dc);

	}
}
