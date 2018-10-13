package cn.itcast.bookstore.book.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

public class BookDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * 全部分类
	 */
	public List<Book> findAll() {
		try {
			String sql = "SELECT * FROM book WHERE del=false";
			return qr.query(sql, new BeanListHandler<Book>(Book.class));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按分类查询
	 */
	public List<Book> findByCategory(String cid) {
		try {
			String sql = "SELECT * FROM book WHERE cid=? AND del=false";
			return qr.query(sql, new BeanListHandler<Book>(Book.class), cid);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 查看图书详情
	 * @param bid
	 * @return
	 */
	public Book load(String bid) {
		try {
			/*
			 * 需要在Book对象中保存Category的信息
			 */
			String sql = "SELECT * FROM book WHERE bid=?";
			Map<String, Object> map = qr.query(sql, new MapHandler(), bid);
			/*
			 * 使用一个map，映射出两个对象，再给这两个对象奖励关系
			 */
			Category category = CommonUtils.toBean(map, Category.class);
			Book book = CommonUtils.toBean(map, Book.class);
			book.setCategory(category);
			return book;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public int getCountByCid(String cid) {
		try {
			String sql = "SELECT COUNT(*) FROM book WHERE cid=?";
			Number number = (Number) qr.query(sql, new ScalarHandler(), cid);
			return number.intValue();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 添加图片
	 * @param book
	 */
	public void add(Book book) {
		try {
			String sql = "INSERT INTO book VALUES(?,?,?,?,?,?,?)";
			Object[] params = {book.getBid(), book.getBname(), book.getPrice(), book.getAuthor(),
								book.getImage(), book.getCategory().getCid(), book.isDel()};
			qr.update(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 删除图片（伪）
	 */
	public void delete(String bid) {
		try {
			String sql = "UPDATE book SET del=true WHERE bid=?";
			qr.update(sql, bid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 修改图片
	 * @param book
	 */
	public void modify(Book book) {
		// TODO Auto-generated method stub
		try {
			String sql = "UPDATE book SET bname=?,price=?,author=?,cid=?,image=?,del=? WHERE bid=?";
			Object[] params = {book.getBname(), book.getPrice(), book.getAuthor(), book.getCategory().getCid(),
							book.getImage(),book.isDel(), book.getBid()};
			qr.update(sql, params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
