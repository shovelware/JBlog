package nl.cerios.blog;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BlogServlet extends HttpServlet{
	private static final long serialVersionUID = -73186648007060644L;
	
	private PostFetcher fetcher = new PostFetcher();

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
    	try{

    		ArrayList<Post> posts = fetcher.fetchRecentPosts(5);

    		request.setAttribute("posts", posts);
    		RequestDispatcher rd = request.getRequestDispatcher("posts.jsp");
    		rd.forward(request, response);
    		
    	}
    	catch (Exception e){
    		System.err.println(e);
    		response.sendRedirect("http500.jsp");
    	}
    	
    }
}