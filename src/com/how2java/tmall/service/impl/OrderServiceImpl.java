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
	
//	����
	@Transactional(propagation=Propagation.REQUIRED,rollbackForClassName="Exception")
	public float createOrder(Order order, List<OrderItem> ois) {
//		�Ѷ������뵽���ݿ���
		save(order);
		float total =0;
//		Ϊÿ��OrderItem�����䶩��
		for (OrderItem oi: ois) {
	        oi.setOrder(order);
	        orderItemService.update(oi);
//	        �ۼƽ�����
	        total+=oi.getProduct().getPromotePrice()*oi.getNumber();
	    }
		return total;
	}

//	��ѯuser���е�״̬����"delete" �Ķ�������os
	@Override
	public List<Order> listByUserWithoutDelete(User user){
		DetachedCriteria dc = DetachedCriteria.forClass(clazz);
		dc.add(Restrictions.eq("user", user));
//		status������delete
		dc.add(Restrictions.ne("status", OrderService.delete));
		return findByCriteria(dc);

	}
}
