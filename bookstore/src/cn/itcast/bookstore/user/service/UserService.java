package cn.itcast.bookstore.user.service;

import org.ietf.jgss.Oid;

import cn.itcast.bookstore.user.dao.UserDao;
import cn.itcast.bookstore.user.domain.User;

/**
 * User业务层
 * @description
 *
 * @author yzy
 *
 * @date  2018年8月31日
 */
public class UserService {
	private UserDao userDao = new UserDao();
	
	/**
	 * 注册功能
	 * @throws UserException 
	 */
	public void regist(User from) throws UserException {
		//校验用户名
		User user = userDao.findByUsername(from.getUsername());
		if(user != null) throw new UserException("用户名已被注册");
		//校验邮箱
		user = userDao.findByEmail(from.getEmail());
		if(user != null) throw new UserException("邮箱已被注册");
		
		//插入用户到数据库
		userDao.add(from);
	}
	
	/**
	 * 激活功能
	 * @param code
	 * @throws UserException 
	 */
	public void active(String code) throws UserException {
		/*
		 * 1.使用code查询数据库，得到User
		 */
		User user = userDao.findByCode(code);
		/*
		 * 2.如果user不存在，说明激活码错误
		 */
		if(user == null) throw new UserException("激活码无效！");
		/*
		 * 3.校验用户的状态是否为激活状态，
		 */
		if(user.isState()) throw new UserException("该用户已激活！");
		/*
		 * 4.修改用户状态
		 */
		userDao.updateState(user.getUid(), true);
	}
	
	/**
	 * 登陆功能
	 * @param username
	 * @param password
	 * @throws UserException 
	 */
	public User login(User form) throws UserException {
		//根据username查询用户
		User user = userDao.findByUsername(form.getUsername());
		//若果为空则不存在还用户，返回错误信息
		if(user == null) throw new UserException("用户不存在！");
		//如果用户存在，校验密码是否一致
		if(!user.getPassword().equals(form.getPassword())) {
			throw new UserException("密码错误！");
		}
		if(form.isState()) throw new UserException("该用户尚未激活！");
		return user;
	}
}
