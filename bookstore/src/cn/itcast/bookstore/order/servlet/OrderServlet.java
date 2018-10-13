package cn.itcast.bookstore.order.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.cart.domain.Cart;
import cn.itcast.bookstore.cart.domain.CartItem;
import cn.itcast.bookstore.order.domain.Order;
import cn.itcast.bookstore.order.domain.OrderItem;
import cn.itcast.bookstore.order.service.OrderException;
import cn.itcast.bookstore.order.service.OrderService;
import cn.itcast.bookstore.user.domain.User;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

public class OrderServlet extends BaseServlet {
	private OrderService orderService = new OrderService();
	
	/**
	 * 添加订单
	 * 把session中的车用来生成order对象
	 */
	public String add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/**
		 * 1.从session中得到cart
		 * 2.使用cart生成order对象
		 * 3.调用orderservice的add方法添加order
		 * 4.保存order到request域中，转发到/jsps/order/desc.jsp
		 */
		
		/*
		 * 创建订单
		 */
		//从session中获取cart
		Cart cart = (Cart) request.getSession().getAttribute("cart");
		//把cart转换成order对象
		Order order = new Order();
		order.setOid(CommonUtils.uuid());//设置编号
		order.setOrdertime(new Date());//设置下单时间
		order.setState(1);//设置状态
		User user = (User) request.getSession().getAttribute("session_user");
		order.setOwner(user);//设置下单人
		order.setTotal(cart.getTotal());
		
		/*
		 * 创建订单条目
		 */
		//创建订单条目集合
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		//循环遍历cart中的所有cartItem，使用每一个CartItem对象创建OredrItem对象，并添加到集合中
		for(CartItem cartItem : cart.getCartItems()) {
			//创建订单条目
			OrderItem orderItem = new  OrderItem();
			
			orderItem.setIid(CommonUtils.uuid());
			orderItem.setCount(cartItem.getCount());
			orderItem.setSubtotal(cartItem.getSubtotal());
			orderItem.setOrder(order);
			orderItem.setBook(cartItem.getBook());
			
			//把订单条目添加到集合中
			orderItemList.add(orderItem);
		}
		//将集合添加到订单中
		order.setOrderItemList(orderItemList);
		//清空购物车
		cart.clear();
		
		/*
		 * 调用orderService的add方法,添加order
		 */
		orderService.add(order);
		
		/*
		 * 保存order到request域中，转发到/jsps/order/desc.jsp
		 */
		request.setAttribute("order", order);
		return "f:/jsps/order/desc.jsp";
	}
	
	/**
	 * 我的订单
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String myOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1.从session中获取当前用户，在获取uid
		 * 2.使用uid调用orderService#myorders(uid)，得到该用户的所有订单List<Order>
		 * 3.把订单列表保存到request域中，转发/jsps/order/list.jsp
		 */
		User user = (User) request.getSession().getAttribute("session_user");
		String uid = user.getUid();
		List<Order> orders = orderService.myorders(uid);
		request.setAttribute("orders", orders);
		return "f:/jsps/order/list.jsp";
	}
	
	/**
	 * 加载订单
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String load(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//得到oid
		String oid = request.getParameter("oid");
		//调用service#findByOid得到order
		Order order = orderService.findByOid(oid);
		request.setAttribute("order", order);
		return "f:/jsps/order/desc.jsp";
	}
	
	/**
	 * 确认收货
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String comfirm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1.获取oid参数
		 * 2.调用service方法，如果有异常，保存异常信息，转发到msg.jsp,否则保存成功信息，转发到msg.jsp
		 */
		String oid = request.getParameter("oid");
		try {
			orderService.confirm(oid);
			request.setAttribute("msg", "确认收货成功！");
		} catch (OrderException e) {
			request.setAttribute("msg", e.getMessage());
		}
		return "f:/jsps/msg.jsp";
	}
}
