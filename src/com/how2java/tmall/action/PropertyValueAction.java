package com.how2java.tmall.action;

import org.apache.struts2.convention.annotation.Action;



public class PropertyValueAction extends Action4Result {

	
	@Action("admin_propertyValue_edit")
	public String edit() {
		//初始化
		t2p(product);
		propertyValueService.init(product);
//		根据产品，获取其对应的属性值集合
		propertyValues = propertyValueService.listByParent(product); 
		return "editPropertyValue";
	}
	@Action("admin_propertyValue_update")
	public String update() {
//		获取页面传递过来的属性值
		String value = propertyValue.getValue();
		t2p(propertyValue);
		propertyValue.setValue(value);
		propertyValueService.update(propertyValue);
		return "success.jsp";
	}
}
