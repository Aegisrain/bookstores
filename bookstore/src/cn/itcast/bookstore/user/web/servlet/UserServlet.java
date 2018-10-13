package cn.itcast.bookstore.user.web.servlet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.cart.domain.Cart;
import cn.itcast.bookstore.user.domain.User;
import cn.itcast.bookstore.user.service.UserException;
import cn.itcast.bookstore.user.service.UserService;
import cn.itcast.commons.CommonUtils;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;
import cn.itcast.servlet.BaseServlet;

/**
 * User表述层
 * @description
 *
 * @author yzy
 *
 * @date  2018年8月31日
 */
public class UserServlet extends BaseServlet {
	private UserService userService = new UserService();
	
	/**
	 * 激活功能
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1.获取参数激活码
		 * 2.调用service的active方法
		 * 3.若有异常，保存异常信息到request域中，转发到msg.jsp
		 * 4.保存成功信息到request域中，转发到msg.jsp
		 */
		String code = request.getParameter("code");
		System.out.println(code);
		try {
			userService.active(code);
			request.setAttribute("msg", "激活成功！");
		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());		
		}
		return "f:/jsps/msg.jsp";
	}
	
	/**
	 * 注册
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String regist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//封装表单数据
		User form = CommonUtils.toBean(request.getParameterMap(), User.class);
		//补全uid、code
		form.setUid(CommonUtils.uuid());
		form.setCode(CommonUtils.uuid() + CommonUtils.uuid());
		//表单数据校验
		Map<String, String> erros = new HashMap<String, String>();
		String username = form.getUsername();
		if(username == null || username.trim().isEmpty()) {
			erros.put("username", "用户名不能为空！");
		}else if(username.length() < 3 || username.length() > 10) {
			erros.put("username", "用户名长度错误！");
		}
		
		String password = form.getPassword();
		if(password == null || password.trim().isEmpty()) {
			erros.put("password", "密码不能为空！");
		}else if(password.length() < 3 || password.length() > 10) {
			erros.put("password", "密码长度错误！");
		}
		
		String email = form.getEmail();
		if(email == null || email.trim().isEmpty()) {
			erros.put("email", "邮箱不能为空！");
		}else if(!email.matches("\\w+@\\w+\\.\\w+")) {
			System.out.println(email);
			erros.put("email", "邮箱格式错误！");
		}
		
		//判断是否存在错误信息
		if(erros.size() > 0) {
			//保存错误信息
			request.setAttribute("erros", erros);
			//保存表单数据，用于回显
			request.setAttribute("form", form);
			//转发到regist.jsp
			return "f:/jsps/user/regist.jsp";
		}
		
		//调用service的regist方法
		try { 
			userService.regist(form);

		} catch (Exception e) {
			/*
			 * 1.保存错误信息
			 * 2.保存到form
			 * 3.转发到regist.jsp
			 */
			request.setAttribute("msg", e.getMessage());
			request.setAttribute("form", form);
			return "f:/jsps/user/regist.jsp";
		}
		
		/*
		 * 发邮件
		 * 1.准备配置文件
		 * 2.获取配置文件内容
		 */
		Properties props = new Properties();
		//加载配置文件
		props.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
		//获取各项参数
		String host = props.getProperty("host");
		String uname = props.getProperty("uname");
		String pwd = props.getProperty("pwd");
		String from = props.getProperty("from");
		String to = form.getEmail();
		String subject = props.getProperty("subject");
		String content = props.getProperty("content");
		content = MessageFormat.format(content,	form.getCode());//替换{0}

		try {
			Session session = MailUtils.createSession(host, uname, pwd);
			Mail mail = new Mail(from, to, subject, content);//创建邮件对象
			MailUtils.send(session, mail);//发送邮件
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		
		/*
		 * 1.保存成功信息
		 * 2.转发到msg.jsp
		 */
		request.setAttribute("msg", "恭喜注册成功！请到邮箱点击链接注册激活！");
		return "f:/jsps/msg.jsp"; 
	}
	
	/**
	 * 登陆功能
	 */
	public String login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//封装表单数据
		User form = CommonUtils.toBean(request.getParameterMap(), User.class); 
		try {
			User user = userService.login(form);
			//保存成功信息
			request.getSession().setAttribute("session_user", user);
			//给用户添加一个购物车，即向session中保存一个cart对象
			request.getSession().setAttribute("cart", new Cart());
			//重定向到index.jsp
			return "r:/index.jsp";
		} catch (UserException e) {
			//保存错误信息
			request.setAttribute("msg", e.getMessage());
			//保存表单信息，用与回显
			request.setAttribute("form", form);
			//转发到login.jsp
			return "f:/jsps/user/login.jsp";
		}
	}
	
	/**
	 * 退出功能
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String quit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//销毁session
		request.getSession().invalidate();
		//重定向
		return "r:/index.jsp";
	}
}
