package com.how2java.tmall.action;
 
import org.apache.struts2.convention.annotation.Action;

import com.how2java.tmall.util.Page;
 
public class PropertyAction extends Action4Result{
	
//	查询
	@Action("admin_property_list")
	public String list() {
//		判断是否有分页对象，如果没有创建一个新的Page对象。新的Page对象即代表第一个，本页有5条数据。
		if(page==null)
			page = new Page();
		int total = propertyService.total(category);
		page.setTotal(total);
//		为分页对象设置参数
		page.setParam("&category.id="+category.getId());
		propertys = propertyService.list(page,category);
//		让category引用从指向瞬时对象，变为指向持久对象
		t2p(category);
		return "listProperty";
	}
	
	@Action("admin_property_add")
	public String add() {
		propertyService.save(property);
		return "listPropertyPage";
	}
	
	@Action("admin_property_delete")
	public String delete() {
		t2p(property);
		propertyService.delete(property);
		return "listPropertyPage";
	}
	
	@Action("admin_property_edit")
	public String edit() {
		t2p(property);
		return "editProperty";
	}
	@Action("admin_property_update")
	public String update() {
		propertyService.update(property);
		return "listPropertyPage";
	}

}
