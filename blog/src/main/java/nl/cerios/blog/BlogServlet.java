package nl.cerios.blog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.yaml.snakeyaml.Yaml;

import nl.cerios.blog.database.ConnectionFactory;
import nl.cerios.blog.database.PostDAOSQL;
import nl.cerios.blog.database.PostDTO;

public class BlogServlet extends HttpServlet{
	private static final long serialVersionUID = -73186648007060644L;
	
	private PostDAOSQL postDB = new PostDAOSQL();
	private int postCount = 2;
	
	public void init(ServletConfig config)
			   throws ServletException {
		
		super.init(config);
		
		try{
			Yaml yaml = new Yaml();
			
				File yamlConfig = new File(getServletContext().getRealPath("/static/config.yml"));
				InputStream input = new FileInputStream(yamlConfig);
				
				AppConfiguration appConfig = yaml.loadAs(input,  AppConfiguration.class);

				postCount = appConfig.getPostsToRetrieve();
				input.close();
			
			ConnectionFactory.getInstance().init(appConfig);
		}
		
		catch(Exception e){
			System.err.println("Servlet init: " + e);
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)  
            throws ServletException, IOException {
		try{
		String url = request.getRequestURI();
		
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/http500.jsp");
		
			switch (url) {
			case "/blog/post/submit":
				String ptitle = (String) request.getParameter("title");
				String ptext = (String) request.getParameter("text");

				PostDTO post = new PostDTO(1, 3, LocalDateTime.now(), ptitle, ptext);
				postDB.InsertPost(post);

				rd = getServletContext().getRequestDispatcher("/postRecent.jsp");
				break;

			case "/blog/blog/submit":
				System.out.println("Pretending to submit a blog");

				rd = getServletContext().getRequestDispatcher("/blogView.jsp");
				break;

			case "/blog/profile/submit":
				System.out.println("Pretending to submit a profile");

				rd = getServletContext().getRequestDispatcher("/profileView.jsp");
				break;
				
			case "/blog/login/submit":
				String username = (String) request.getParameter("username");
				String password = (String) request.getParameter("password");
				
				boolean authenticated = username.equals(password);
				
				if (authenticated)
				{
					request.getSession().setAttribute("loggedInUser", username);
					rd = getServletContext().getRequestDispatcher("/index.jsp");
				}
				
				else
				{
					request.setAttribute("errorMessage", "Login Failed, please try again");
					rd = getServletContext().getRequestDispatcher("/login.jsp");
				}
				break;
			}
			
			rd.forward(request, response);
		}

		catch (Exception e) {
			System.err.println("Servlet POST " + e);
			request.setAttribute("errdetails", e.getMessage());
			response.sendRedirect("httperr.jsp");
		}
	}

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	try{
    		String url = request.getRequestURI();
    		
    		RequestDispatcher rd = getServletContext().getRequestDispatcher("/http500.jsp");
    		
    		switch (url)
    		{
    			//Homepage
    		case "/blog/":
    			rd = getServletContext().getRequestDispatcher("/index.jsp");
    			break;
    		case "/blog/index":
    			rd = getServletContext().getRequestDispatcher("/index.jsp");
    			break;
    			
    			//About
    		case "/blog/about":
    			rd = getServletContext().getRequestDispatcher("/about.jsp");
    			break;
    			
    			//Login
    		case "/blog/login":
    			rd=getServletContext().getRequestDispatcher("/login.jsp");
    			break;
    			
    			//Recent posts
    		case "/blog/recent":
        		List<PostDTO> posts = postDB.getPostByTimestamp(LocalDateTime.now(), postCount);
        		request.setAttribute("posts", posts);
    
        		rd = getServletContext().getRequestDispatcher("/postRecent.jsp");
    			break;
    			
    			//View post
    		case "/blog/post":
    			
    			// TODO - fetch real data from DB
    			PostDTO post = new PostDTO(123,2, LocalDateTime.now(), "Test Title", "Test Tekst");
    			
    			request.setAttribute("post", post);
    			rd = getServletContext().getRequestDispatcher("/postView.jsp");
    			break;
    			
    			//Write new post
    		case "/blog/post/edit":
    			rd = getServletContext().getRequestDispatcher("/postEdit.jsp");
    			break;
    			
    			
    			//View blog
    		case "/blog/blog":
    			rd = getServletContext().getRequestDispatcher("/blogView.jsp");
    			break;
    			
    			//Add new blog
    		case "/blog/blog/edit":
    			rd = getServletContext().getRequestDispatcher("/blogEdit.jsp");
    			break;
    			
    			
    			//View profile
    		case "/blog/profile":
    			rd = getServletContext().getRequestDispatcher("/profileView.jsp");
    			break;
    			
    			//Write new post
    		case "/blog/profile/edit":
    			rd = getServletContext().getRequestDispatcher("/profileEdit.jsp");
    			break;
    			
    			//Logout
			case "/blog/logout":
				request.getSession().removeAttribute("loggedInUser");
				rd = getServletContext().getRequestDispatcher("/index.jsp");
				break;
    			
    		default:
        		String rc = request.getContextPath();
        		String sc = getServletContext().getContextPath();
        		System.out.println(rc + " | " + sc + " | " + url);
    			break;
    		}
    		
    		rd.forward(request, response);
    	}
    	catch (ServletException e){
    		System.err.println("Servlet GET " + e);
    		request.setAttribute("errdetails", e.getMessage());
    		response.sendRedirect("httperr.jsp");
    	}
    	
    }
}