package cn.itcast.bookstore.book.web.servlet.admin;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.book.service.BookService;
import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.bookstore.category.service.CategoryService;
import cn.itcast.commons.CommonUtils;

public class AdminAddBookServlet extends HttpServlet {
	private BookService bookService = new BookService();
	private CategoryService categoryService = new CategoryService();
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		/*
		 * 1.把表单数据封装到Book对象中
		 * 	>上传三步
		 */
		//创建工厂
		DiskFileItemFactory factory = new DiskFileItemFactory(15 * 1024, new File("/F:/f/temp"));
		//得到解析器
		ServletFileUpload sfu = new ServletFileUpload(factory);
		//设置单个文件的大小为15kb
		sfu.setFileSizeMax(15 * 1024);
		//使用解析器去解析request对象，得到List<FileItem>
		try {
			List<FileItem> fileItems = sfu.parseRequest(request);
			/*
			 * 把FileItem中的数据封装都Book对象中
			 * >把所有普通表单数据封装到Map中
			 * >再把Map中的数据封装到Book对象中
			 */
			Map<String, Object> map = new HashMap<String, Object>();
			for(FileItem fileItem : fileItems) {
				if(fileItem.isFormField()) {
					map.put(fileItem.getFieldName(), fileItem.getString("UTF-8"));
				}
			}
			Book book = CommonUtils.toBean(map, Book.class);
			//为book设置bid
			book.setBid(CommonUtils.uuid());
			book.setDel(false);
			Category category = CommonUtils.toBean(map, Category.class);
			book.setCategory(category);
			/*
			 * 保存上传的文件
			 */			

			
			//得到保存目录
			String savePath = this.getServletContext().getRealPath("/book_img");
			//得到文件名称,在前面加上UUID，方便区分
			String filename = CommonUtils.uuid() + "_" + fileItems.get(1).getName();
			
			/*
			 * 校验文件扩展名
			 */
			if(!filename.toLowerCase().endsWith("jpg")) {
				request.setAttribute("msg", "上传格式仅支持  '.jpg'");
				request.setAttribute("categoryList", categoryService.findAll());
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp").forward(request, response);
				return;
			}
			
			//使用目录和文件名创建目标文件
			System.out.println("savepath: " + savePath +",   " + filename);
			File destfile = new File(savePath, filename);
			//保存上传文件到目标文件
			fileItems.get(1).write(destfile);
			
			/*
			 * 设置Book对象的image
			 */
			book.setImage("book_img/" + filename);
			
			/*
			 * 调用service的方法添加book
			 */
			bookService.add(book);
			
			/*
			 * 校验图片的尺寸
			 */
			Image image = new ImageIcon(destfile.getAbsolutePath()).getImage();
			if(image.getHeight(null) > 200 || image.getWidth(null) > 200) {
				//删除图片
				destfile.delete();
				request.setAttribute("msg", "上传文件尺寸超过了 200*200，请重新上传！");
				request.setAttribute("categoryList", categoryService.findAll());
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp").forward(request, response);
				return;
			}
			
			/*
			 * 转发到图书列表
			 */
			request.getRequestDispatcher("/admin/AdminBookServlet?method=findAll").forward(request, response);;
		} catch (Exception e) {
			if(e instanceof FileUploadBase.FileSizeLimitExceededException) {
				request.setAttribute("msg", "上传文件大小超过15kb，请重新上传！");
				request.setAttribute("categoryList", categoryService.findAll());
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp").forward(request, response);
			}
			throw new RuntimeException(e);
		}
	}

}
