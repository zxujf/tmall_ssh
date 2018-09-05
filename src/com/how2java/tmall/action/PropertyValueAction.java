package com.how2java.tmall.action;

import org.apache.struts2.convention.annotation.Action;



public class PropertyValueAction extends Action4Result {

	
	@Action("admin_propertyValue_edit")
	public String edit() {
		//��ʼ��
		t2p(product);
		propertyValueService.init(product);
//		���ݲ�Ʒ����ȡ���Ӧ������ֵ����
		propertyValues = propertyValueService.listByParent(product); 
		return "editPropertyValue";
	}
	@Action("admin_propertyValue_update")
	public String update() {
//		��ȡҳ�洫�ݹ���������ֵ
		String value = propertyValue.getValue();
		t2p(propertyValue);
		propertyValue.setValue(value);
		propertyValueService.update(propertyValue);
		return "success.jsp";
	}
}
