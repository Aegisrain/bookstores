package cn.itcast.bookstore.book.service;

import java.sql.SQLException;
import java.util.List;

import com.sun.mail.util.QEncoderStream;

import cn.itcast.bookstore.book.dao.BookDao;
import cn.itcast.bookstore.book.domain.Book;

public class BookService {
	private BookDao bookDao = new BookDao();
	
	
	/**
	 * 查询所有图书
	 */
	public List<Book> findAll() {
		return bookDao.findAll();
	}

	/**
	 * 按分类查询
	 * @return
	 */
	public List<Book> findByCategory(String cid) {
		return bookDao.findByCategory(cid);
	}
	
	/**
	 * 查看图书详情
	 * @param bid
	 * @return
	 */
	public Book load(String bid) {
		return bookDao.load(bid);
	}

	/**
	 * 添加图书
	 * @param book
	 */
	public void add(Book book) {
		bookDao.add(book);
	}
	
	/**
	 * 删除图片（伪）
	 * @param bid
	 */
	public void delete(String bid) {
		bookDao.delete(bid);
	}

	/**
	 * 修改图片
	 * @param book
	 */
	public void modify(Book book) {
		// TODO Auto-generated method stub
		bookDao.modify(book);
	}
}
