package nl.cerios.blog;

import java.io.*;
import javax.servlet.ServletException;  
import javax.servlet.http.*;

public class BlogServlet extends HttpServlet{
	private static final long serialVersionUID = -73186648007060644L;

	public String hallo(String name){
		String hw = "Hello, " + name + ".";
		return hw;
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)  
            throws ServletException, IOException {
		response.sendRedirect("http404.jsp");
	}
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	response.sendRedirect("http404.jsp");
    }
}