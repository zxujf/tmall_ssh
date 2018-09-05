package com.how2java.tmall.action;

import java.lang.reflect.Method;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.OrderItemService;
import com.how2java.tmall.service.OrderService;
import com.how2java.tmall.service.ProductImageService;
import com.how2java.tmall.service.ProductService;
import com.how2java.tmall.service.PropertyService;
import com.how2java.tmall.service.PropertyValueService;
import com.how2java.tmall.service.ReviewService;
import com.how2java.tmall.service.UserService;

//�ṩ�����ע��
public class Action4Service extends Action4Pojo{

	@Autowired
	CategoryService categoryService;
	@Autowired
	PropertyService propertyService;
	@Autowired
	ProductService productService;	
	@Autowired
	ProductImageService productImageService;	
	@Autowired
	PropertyValueService propertyValueService;	
	@Autowired
	UserService userService;	
	@Autowired
	OrderService orderService;	
	@Autowired
	OrderItemService orderItemService;	
	@Autowired
	ReviewService reviewService;	
	/**
	 * transient to persistent
	 * ˲ʱ����ת��Ϊ�־ö���
	 * @param o
	 */
	public void t2p(Object o){
			try {
				//��ȡ˲ʱ����������
				Class clazz = o.getClass();
				//ͨ�����䣬�������˲ʱ�����getId������ȡid
				int id = (Integer) clazz.getMethod("getId").invoke(o);
				//����id����ȡ�־û�����
				Object persistentBean = categoryService.get(clazz, id);

				String beanName = clazz.getSimpleName();
				Method setMethod = getClass().getMethod("set" + WordUtils.capitalize(beanName), clazz);
				//ͨ�����䣬�Ѹó־ö���������category������
				setMethod.invoke(this, persistentBean);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
