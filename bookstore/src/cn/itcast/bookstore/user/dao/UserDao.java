package cn.itcast.bookstore.user.dao;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import cn.itcast.bookstore.user.domain.User;
import cn.itcast.jdbc.TxQueryRunner;

/**
 * User持久层
 * @description
 *
 * @author yzy
 *
 * @date  2018年8月31日
 */
public class UserDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * 按用户名查询
	 * @param username
	 * @return
	 */
	public User findByUsername(String username) {
		try {
			String sql = "select * from user where username=?";
			return qr.query(sql, new BeanHandler<User>(User.class), username);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按邮件查询
	 * @param email
	 * @return
	 */
	public User findByEmail(String email) {
		try {
			String sql = "select * from user where email=?";
			return qr.query(sql, new BeanHandler<User>(User.class), email);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按激活码查询
	 */
	public User findByCode(String code) {
		try {
			String sql = "select * from user where code=?";
			return qr.query(sql, new BeanHandler<User>(User.class), code);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 修改指定用户的状态
	 * @param uid
	 * @param state
	 */
	public void updateState(String uid, boolean state) {
		try {
			String sql = "UPDATE user SET state=? WHERE uid=?";
			qr.update(sql, state, uid);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 向数据库插入数据
	 */
	public void add(User user) {
		try {
			String sql = "INSERT INTO user VALUES (?, ?, ?, ?, ?, ?)";
			Object[] params = {user.getUid(), user.getUsername(), user.getPassword(),
					user.getEmail(), user.getCode(), user.isState()};
			qr.update(sql, params);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
