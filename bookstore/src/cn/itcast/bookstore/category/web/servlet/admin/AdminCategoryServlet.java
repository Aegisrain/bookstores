package cn.itcast.bookstore.category.web.servlet.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.bookstore.category.service.CategoryService;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

public class AdminCategoryServlet extends BaseServlet {
	private CategoryService categoryService = new CategoryService();
	
	/**
	 * 查询所有分类
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1.调用service方法,得到所有分类
		 * 2.保存到request域中，
		 * 3.转发到/adminjsps/admin/category/list.jsp
		 */
		request.setAttribute("categoryList", categoryService.findAll());
		return "f:/adminjsps/admin/category/list.jsp";
	}
	
	/**
	 * 添加分类
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 1.封装表单数据
		 * 2.补全cid
		 * 3.调用service方法，添加分类
		 * 4.电泳findAll()
		 */
		Category category = CommonUtils.toBean(request.getParameterMap(), Category.class);
		//补全cid
		category.setCid(CommonUtils.uuid());
		categoryService.add(category);
		return findAll(request, response);
	}
	
	/**
	 * 删除分类
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获取参数cid
		String cid = request.getParameter("cid");
		//调用service方法，传递cid参数
		try {
			categoryService.delete(cid);
			return findAll(request, response);
		} catch (CategoryException e) {
			request.setAttribute("msg", e.getMessage());
			return "f:/adminjsps/msg.jsp";
		}
		
	}
	
	/**
	 * 修改分类之前的加载工作
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String modifyPre(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获取cid
		String cid = request.getParameter("cid");
		//通过cid调用service方法，得到Category对象
		Category category = categoryService.getCategoryById(cid);
		//将category保存到request域，转发到mod.jsp
		request.setAttribute("category", category);
		System.out.println(category);
		return "f:/adminjsps/admin/category/mod.jsp";
	}
	
	/**
	 * 修改分类
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String modify(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//封装表单数据
		Category category = CommonUtils.toBean(request.getParameterMap(), Category.class);
		//通过cid调用service方法，修改分类
		categoryService.modify(category);
		return findAll(request, response);
	}
}
