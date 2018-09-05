package com.how2java.tmall.interceptor;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsStatics;
import org.springframework.beans.factory.annotation.Autowired;

import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.User;
import com.how2java.tmall.service.OrderItemService;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class CartTotalItemNumberInterceptor extends AbstractInterceptor {

	@Autowired
	OrderItemService orderItemService;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		
		ActionContext ctx = invocation.getInvocationContext();
		HttpServletRequest request = (HttpServletRequest) ctx.get(StrutsStatics.HTTP_REQUEST);
		ServletContext servletContext = (ServletContext) ctx.get(StrutsStatics.SERVLET_CONTEXT);

		String contextPath = servletContext.getContextPath();
		String uri = request.getRequestURI();
		uri = StringUtils.remove(uri, contextPath);
		if (uri.startsWith("/fore")) {
//			是否登陆
			User user = (User) ActionContext.getContext().getSession().get("user");
//			如果未登陆，则把session的cartTotalItemNumber设置为0.
			int cartTotalItemNumber = 0;
			if (null != user) {
				ctx.getSession().put("cartTotalItemNumber", 0);
//			如果登陆了，那么就把当前用户的未设置订单的订单项取出来，并累计其中的数量，然后放在session的"cartTotalItemNumber" 上
			} else {
				List<OrderItem> ois = orderItemService.list("user", user, "order", null);
				for (OrderItem oi : ois)
					cartTotalItemNumber += oi.getNumber();
				ctx.getSession().put("cartTotalItemNumber", cartTotalItemNumber);
			}
		}
		return invocation.invoke();
	}

}
