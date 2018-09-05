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

//提供服务的注入
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
	 * 瞬时对象转换为持久对象
	 * @param o
	 */
	public void t2p(Object o){
			try {
				//获取瞬时对象的类对象
				Class clazz = o.getClass();
				//通过反射，调用这个瞬时对象的getId方法获取id
				int id = (Integer) clazz.getMethod("getId").invoke(o);
				//根据id，获取持久化对象
				Object persistentBean = categoryService.get(clazz, id);

				String beanName = clazz.getSimpleName();
				Method setMethod = getClass().getMethod("set" + WordUtils.capitalize(beanName), clazz);
				//通过反射，把该持久对象设置在category引用上
				setMethod.invoke(this, persistentBean);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
