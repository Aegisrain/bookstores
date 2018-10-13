package cn.itcast.bookstore.category.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.jdbc.TxQueryRunner;

public class CategoryDao {
	private QueryRunner qr = new TxQueryRunner();

	/**
	 * 查询所有分类
	 * @return
	 */
	public List<Category> findAll() {
		String sql = "SELECT * FROM category";
		try {
			return qr.query(sql, new BeanListHandler<Category>(Category.class));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 添加分类
	 * @param category
	 */
	public void add(Category category) {
		// TODO Auto-generated method stub
		try {
			String sql = "INSERT INTO category VALUES(?,?)";
			qr.update(sql, category.getCid(), category.getCname());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 删除分类
	 * @param cid
	 */
	public void delete(String cid) {
		// TODO Auto-generated method stub
		try {
			String sql = "DELETE from category WHERE cid=?";
			qr.update(sql, cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 通过cid查找分类
	 * @param cid
	 * @return
	 */
	public Category getCategoryById(String cid) {
		try {
			String sql = "SELECT * from category WHERE cid=?";
			return qr.query(sql, new BeanHandler<Category>(Category.class), cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 修改分类
	 * @param cid
	 */
	public void modify(Category category) {
		try {
			String sql = "UPDATE category SET cname=? WHERE cid=?";
			qr.update(sql, category.getCname(), category.getCid());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
}
