package cn.itcast.bookstore.cart.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 购物车
 * @description
 *
 * @author yzy
 *
 * @date  2018年9月3日
 */
public class Cart {
	private Map<String, CartItem> map = new LinkedHashMap<String, CartItem>();
	
	/**
	 * 合计
	 * @return
	 */
	public double getTotal() {
		/**
		 * 二进制可能会产生误差，需要将其转换成BigDecimal进行计算
		 */
		BigDecimal total = new BigDecimal("0");
		for(CartItem cartItem : map.values()) {
			BigDecimal c = new BigDecimal(cartItem.getSubtotal() + "");
			total = total.add(c);
		}
		return total.doubleValue();
	}
	
	/**
	 * 添加条目
	 * @param cartItem
	 */
	public void add(CartItem cartItem) {
		if(map.containsKey(cartItem.getBook().getBid())) {
			CartItem _cartItem = map.get(cartItem.getBook().getBid());
			_cartItem.setCount(_cartItem.getCount() + cartItem.getCount());
			map.put(_cartItem.getBook().getBid(), _cartItem);
		}else {
			map.put(cartItem.getBook().getBid(), cartItem);
		}
	}
	
	/**
	 * 清空购物车
	 */
	public void clear() {
		map.clear();
	}
	
	/**
	 * 删除条目
	 * @param bid
	 */
	public void delete(String bid) {
		map.remove(bid);
	}
	
	/**
	 * 我的购物车 
	 * @return
	 */
	public Collection<CartItem> getCartItems() {
		return map.values();
	}
}
