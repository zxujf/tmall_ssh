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
	
//	初始化PropertyValue
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
//		根据产品获取分类，然后获取这个分类下的所有属性集合
		List<PropertyValue> result= this.list("property",property, "product",product);
//		已经存在属性值?
		if(result.isEmpty())
			return null;
//		如果不存在，那么就创建一个属性值，并设置其属性和产品，接着插入到数据库中
		return result.get(0);
		
	}
	
	

	
}
