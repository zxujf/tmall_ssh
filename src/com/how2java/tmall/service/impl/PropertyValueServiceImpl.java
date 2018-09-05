package com.how2java.tmall.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.pojo.PropertyValue;
import com.how2java.tmall.service.PropertyService;
import com.how2java.tmall.service.PropertyValueService;

@Service
public class PropertyValueServiceImpl extends BaseServiceImpl implements PropertyValueService {

	@Autowired
	PropertyService propertyService; 
	
//	��ʼ��PropertyValue
	@Override
	public void init(Product product) {
		List<Property> propertys= propertyService.listByParent(product.getCategory());
		for (Property property: propertys) {
			PropertyValue propertyValue = get(property,product);
			if(null==propertyValue){
				propertyValue = new PropertyValue();
				propertyValue.setProduct(product);
				propertyValue.setProperty(property);
				save(propertyValue);
			}
		}
	}

	private PropertyValue get(Property property, Product product) {
//		���ݲ�Ʒ��ȡ���࣬Ȼ���ȡ��������µ��������Լ���
		List<PropertyValue> result= this.list("property",property, "product",product);
//		�Ѿ���������ֵ?
		if(result.isEmpty())
			return null;
//		��������ڣ���ô�ʹ���һ������ֵ�������������ԺͲ�Ʒ�����Ų��뵽���ݿ���
		return result.get(0);
		
	}
	
	

	
}
