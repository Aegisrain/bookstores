package cn.itcast.bookstore.order.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.order.domain.Order;
import cn.itcast.bookstore.order.domain.OrderItem;
import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

public class OrderDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * 添加订单
	 */
	public void addOrder(Order order) {
		try {
			String sql = "INSERT INTO orders VALUES(?,?,?,?,?,?)";
			/*
			 * 处理util的date转换成sql的timestamp
			 */
			Timestamp timestamp = new Timestamp(order.getOrdertime().getTime());
			Object[] params = {order.getOid(), timestamp, order.getTotal(), 
					order.getState(), order.getOwner().getUid(), order.getAddress()};
			qr.update(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 添加订单条目
	 */
	public void addOrderItemList(List<OrderItem> orderItemList) {
		try {
			String sql = "INSERT INTO orderitem VALUES(?,?,?,?,?)";
			/**
			 * 将orderItemList转换成二维数组
			 */
			Object[][] params = new Object[orderItemList.size()][];
			//循环遍历orderItemList，使用每个orderitem对象为params中每个以为数组赋值
			for(int i = 0; i < orderItemList.size(); i++) {
				OrderItem item = orderItemList.get(i);
				params[i] = new Object[]{item.getIid(), item.getCount(), item.getSubtotal(), 
						item.getOrder().getOid(), item.getBook().getBid()};
			}
			//执行批处理
			qr.batch(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按uid查询订单
	 * @param uid
	 * @return
	 */
	public List<Order> findByUid(String uid) {
		/*
		 * 1.通过uid查询当前用户的所有List<Order>
		 * 2.循环遍历每个Order，加载其所有的OrderItem
		 */
		try {
			String sql = "SELECT * FROM orders WHERE uid=?";
			List<Order> orders = qr.query(sql, new BeanListHandler<Order>(Order.class), uid);
			
			//循环遍历Order，得到OrderItem
			for(Order order : orders) {
				//为Order对象加载它的所有订单条目
				loadOrderItems(order);
			}
			return orders;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 加载指定的订单的所有条目
	 * @throws SQLException 
	 */
	private void loadOrderItems(Order order) throws SQLException {
		/*
		 * 使用多表查询
		 */
		String sql = "SELECT * FROM orderitem i, book b WHERE i.bid=b.bid AND oid=?";
		/*
		 * 因为结果集对应的不是一个javabean，所以不能再使用BeanListHandler，而是使用MapListHandler
		 */
		List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler(), order.getOid());
		List<OrderItem> orderItemList = toOrderItemList(mapList);
		order.setOrderItemList(orderItemList);
	}

	/**
	 * 把mapList中每个Map转换成两个对象，并建立关系
	 * @param mapList
	 * @return
	 */
	private List<OrderItem> toOrderItemList(List<Map<String, Object>> mapList) {
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for(Map<String, Object> map : mapList) {
			OrderItem item = toOrderItem(map);
			orderItemList.add(item);
		}
		return orderItemList;
	}

	/**
	 * 把一个Map转换成一个OrderItem对象
	 * @param map
	 * @return
	 */
	private OrderItem toOrderItem(Map<String, Object> map) {
		OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		orderItem.setBook(book);
		return orderItem;
	}

	/**
	 * 加载订单
	 * @param oid
	 * @return
	 */
	public Order findByOid(String oid) {
		try {
			String sql = "SELECT * FROM orders WHERE oid=?";
			Order order = qr.query(sql, new BeanHandler<Order>(Order.class), oid);

			loadOrderItems(order);
			return order;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 通过oid查看订单状态
	 * @param oid
	 * @return
	 */
	public int getStateByOid(String oid) {
		try {
			String sql = "SELECT state FROM orders WHERE oid=?";
			return (Integer) qr.query(sql, new ScalarHandler(), oid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 修改订单状态
	 * @param oid
	 * @return
	 */
	public void updateState(String oid, int state) {
		try {
			String sql = "UPDATE orders SET state=? WHERE oid=?";
			qr.update(sql, state, oid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}

