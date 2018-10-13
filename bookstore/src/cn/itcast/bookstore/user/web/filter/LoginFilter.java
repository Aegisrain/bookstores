package cn.itcast.bookstore.user.web.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import cn.itcast.bookstore.user.domain.User;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(
		urlPatterns = { 
				"/japs/cart/*", 
				"/jsps/order/*"
		}, 
		servletNames = { 
				"OrderServlet", 
				"CartServlet"
		})
public class LoginFilter implements Filter {

	public void destroy() {
		
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		/*
		 * 1.从session中获取用户信息
		 * 2.如果存在则放行
		 * 3.否则保存错误信息，转发到login.jps
		 */
		HttpServletRequest  httpServlet = (HttpServletRequest) request;
		User user = (User) httpServlet.getSession().getAttribute("session_user");
		if(user != null) {
			chain.doFilter(request, response);
		}else {
			httpServlet.setAttribute("msg", "请先登陆!");
			httpServlet.getRequestDispatcher("/jsps/user/login.jsp").forward(httpServlet, response);
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
		
	}

}
