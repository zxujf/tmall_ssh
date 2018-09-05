package com.how2java.tmall.interceptor;

import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsStatics;
import org.springframework.beans.factory.annotation.Autowired;

import com.how2java.tmall.pojo.User;
import com.how2java.tmall.service.OrderItemService;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class AuthInterceptor  extends AbstractInterceptor{  		
	
    @Override  
    public String intercept(ActionInvocation invocation) throws Exception {
        String[] noNeedAuthPage = new String[]{
                "home",
                "checkLogin",
                "register",
                "loginAjax",
                "login",
                "product",
                "category",
                "search"};
        
          
        ActionContext ctx = invocation.getInvocationContext();
        HttpServletRequest request= (HttpServletRequest) ctx.get(StrutsStatics.HTTP_REQUEST);
        HttpServletResponse response= (HttpServletResponse) ctx.get(StrutsStatics.HTTP_RESPONSE);
        ServletContext servletContext= (ServletContext) ctx.get(StrutsStatics.SERVLET_CONTEXT);
//      获取contextPath： tmall_ssh
        String contextPath = servletContext.getContextPath();
        String uri = request.getRequestURI();
//      去掉前缀/tmall_ssh
        uri =StringUtils.remove(uri, contextPath);
//        如果访问的地址是/fore开头
        if(uri.startsWith("/fore")){
//        	取出fore后面的字符串，比如是forecart,那么就取出cart
        	String method = StringUtils.substringAfterLast(uri,"/fore" );
//        	判断cart是否是在noNeedAuthPage,如果不在，那么就需要进行登录验证 
        	if(!Arrays.asList(noNeedAuthPage).contains(method)){
            	User user = (User) ctx.getSession().get("user");
//            	如果user对象不存在，就客户端跳转到login.jsp
            	if(null==user){
                    response.sendRedirect("login.jsp");
                    return null;
                }
            }
        }
//              否则就正常执行
        return  invocation.invoke();
    }  
  
    
    

}  
