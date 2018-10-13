package cn.itcast.bookstore.cart.domain;

import java.math.BigDecimal;

import cn.itcast.bookstore.book.domain.Book;

/**
 * 购物车条目
 * @description
 *
 * @author yzy
 *
 * @date  2018年9月3日
 */
public class CartItem {
	private Book book;
	private int count;
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	//小计方法，但没有对应的成员
	public double getSubtotal() {
		/**
		 * 二进制可能会产生误差，需要将其转换成BigDecimal进行计算
		 */
		BigDecimal d1 = new BigDecimal(book.getPrice() + "");
		BigDecimal d2 = new BigDecimal(count + "");
		return d1.multiply(d2).doubleValue();
	}
}
