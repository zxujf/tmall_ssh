package com.how2java.tmall.service.impl;
 
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import com.how2java.tmall.service.BaseService;
import com.how2java.tmall.util.Page;
 
@Service
public class BaseServiceImpl  extends ServiceDelegateDAO implements BaseService {
 
    protected Class clazz;
   /*借助异常处理和反射得到Category.class或者Product.class。
    即要做到哪个类继承了BaseServiceImpl,clazz 就对应哪个类对象*/
    public BaseServiceImpl(){
        try{
            throw new Exception();  
        }
        catch(Exception e){
        	//实例化子类，父类的构造方法一定会被调用 
            StackTraceElement stes[]= e.getStackTrace();
            String serviceImpleClassName=   stes[1].getClassName();
            try {
                Class  serviceImplClazz= Class.forName(serviceImpleClassName);
                String serviceImpleClassSimpleName = serviceImplClazz.getSimpleName();
                String pojoSimpleName = serviceImpleClassSimpleName.replaceAll("ServiceImpl", "");
                String pojoPackageName = serviceImplClazz.getPackage().getName().replaceAll(".service.impl", ".pojo");
                String pojoFullName = pojoPackageName +"."+ pojoSimpleName;
                clazz=Class.forName(pojoFullName);
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        }       
    }
    
 //查询所有的分类
    @Override
    public List list() {
        DetachedCriteria dc = DetachedCriteria.forClass(clazz);
        dc.addOrder(Order.desc("id"));
        return findByCriteria(dc);
    }
    @Override
    public int total() {
        String hql = "select count(*) from " + clazz.getName() ;
        List<Long> l= find(hql);
        if(l.isEmpty())
            return 0;
        Long result= l.get(0);
        return result.intValue();
    }
    @Override
    public List<Object> listByPage(Page page) {
        DetachedCriteria dc = DetachedCriteria.forClass(clazz);
        dc.addOrder(Order.desc("id"));
        return findByCriteria(dc,page.getStart(),page.getCount());
    }
    @Override
    public Integer save(Object object) {
        return (Integer) super.save(object);
    }

    @Override
    public Object get(Class clazz, int id) {
        return super.get(clazz, id);
    }
    

	@Override
	public Object get(int id) {
		return get(clazz, id);
	}
	
    //查询父类下的所有子类对象
	@Override
	public List listByParent(Object parent) {
		//借助反射获取父类的类型名称
		String parentName= parent.getClass().getSimpleName();
		//把第一个字母变成小写
		String parentNameWithFirstLetterLower = StringUtils.uncapitalize(parentName);
		DetachedCriteria dc = DetachedCriteria.forClass(clazz);
		dc.add(Restrictions.eq(parentNameWithFirstLetterLower, parent));
		dc.addOrder(Order.desc("id"));
		return findByCriteria(dc);
	}
    
//	以分页的方式查询父类下的子类对象集合，以为分类和属性为例，就是查询某个分类下前5个属性，或者第5个到第10个属性
	@Override
	public List list(Page page, Object parent) {
		String parentName= parent.getClass().getSimpleName();
		String parentNameWithFirstLetterLower = StringUtils.uncapitalize(parentName);
		DetachedCriteria dc = DetachedCriteria.forClass(clazz);
		dc.add(Restrictions.eq(parentNameWithFirstLetterLower, parent));
		dc.addOrder(Order.desc("id"));
		return findByCriteria(dc,page.getStart(),page.getCount());
	}

//	基于父类对象查看其子类集合总数
	@Override
	public int total(Object parentObject) {
		String parentName= parentObject.getClass().getSimpleName();
		String parentNameWithFirstLetterLower = StringUtils.uncapitalize(parentName);
		
		String sqlFormat = "select count(*) from %s bean where bean.%s = ?";
		String hql = String.format(sqlFormat, clazz.getName(), parentNameWithFirstLetterLower);
		
		List<Long> l= this.find(hql,parentObject);
		if(l.isEmpty())
			return 0;
		Long result= l.get(0);
		return result.intValue();
	}

//	多条件查询
	@Override
	public List list(Object... pairParms) {
//		把这个可变数量的参数，按照key,value,key,value,key,value的预判，取出来，并放进Map里
		HashMap<String,Object> m = new HashMap<>();
		for (int i = 0; i < pairParms.length; i=i+2) 
			m.put(pairParms[i].toString(), pairParms[i+1]);

		DetachedCriteria dc = DetachedCriteria.forClass(clazz);

		//		遍历这个Map,借助DetachedCriteria，按照 key,value的方式设置查询条件
		Set<String> ks = m.keySet();
		for (String key : ks) {
//			应该提供偶数个参数
			if(null==m.get(key))
				dc.add(Restrictions.isNull(key));
			else
				dc.add(Restrictions.eq(key, m.get(key)));
		}
		dc.addOrder(Order.desc("id"));
		return this.findByCriteria(dc);
	}
    
  
}
