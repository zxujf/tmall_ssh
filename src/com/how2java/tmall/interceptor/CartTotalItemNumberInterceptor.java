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
//			�Ƿ��½
			User user = (User) ActionContext.getContext().getSession().get("user");
//			���δ��½�����session��cartTotalItemNumber����Ϊ0.
			int cartTotalItemNumber = 0;
			if (null != user) {
				ctx.getSession().put("cartTotalItemNumber", 0);
//			�����½�ˣ���ô�Ͱѵ�ǰ�û���δ���ö����Ķ�����ȡ���������ۼ����е�������Ȼ�����session��"cartTotalItemNumber" ��
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
