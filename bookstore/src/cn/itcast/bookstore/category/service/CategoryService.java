package cn.itcast.bookstore.category.service;

import java.util.List;

import cn.itcast.bookstore.book.dao.BookDao;
import cn.itcast.bookstore.category.dao.CategoryDao;
import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.bookstore.category.web.servlet.admin.CategoryException;

public class CategoryService {
	private CategoryDao categoryDao = new CategoryDao();
	private BookDao bookDao = new BookDao();
	/**
	 * 查询所有分类
	 * @return
	 */
	public List<Category> findAll() {
		return categoryDao.findAll();
	}

	public void add(Category category) {
		// TODO Auto-generated method stub
		categoryDao.add(category);
	}

	public void delete(String cid) throws CategoryException {
		// TODO Auto-generated method stub
		int count = bookDao.getCountByCid(cid);
		//判断该分类下是否还有图书，是则抛出异常
		if(count > 0) throw new CategoryException("该分类下还有图书，无法删除！");
		//如果没有图书，则执行删除
		categoryDao.delete(cid);
	}

	/**
	 * 通过cid得到category
	 * @param cid
	 * @return
	 */
	public Category getCategoryById(String cid) {
		return categoryDao.getCategoryById(cid);
	}

	/**
	 * 修改分类
	 */
	public void modify(Category category) {
		categoryDao.modify(category);
	}
}
