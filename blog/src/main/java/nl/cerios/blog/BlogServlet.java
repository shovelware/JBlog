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
import javax.servlet.http.HttpSession;

import org.yaml.snakeyaml.Yaml;

import nl.cerios.blog.database.Authenticator;
import nl.cerios.blog.database.ConnectionFactory;
import nl.cerios.blog.database.PostDAOSQL;
import nl.cerios.blog.database.PostDTO;
import nl.cerios.blog.database.ProfileDAOSQL;
import nl.cerios.blog.database.ProfileDTO;

public class BlogServlet extends HttpServlet{
	private static final long serialVersionUID = -73186648007060644L;
	
	private PostDAOSQL postDB = new PostDAOSQL();
	private ProfileDAOSQL profileDB = new ProfileDAOSQL();
	
	private Authenticator authenticator = new Authenticator();
	
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
		
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/httperr.jsp");
		
			switch (url) {
				//Submit edited post
			case "/blog/post/submit":
				if (checkLogin(request.getSession())) {
					String ptitle = (String) request.getParameter("title");
					String ptext = (String) request.getParameter("text");

					PostDTO post = new PostDTO(1, 3, LocalDateTime.now(), ptitle, ptext);
					postDB.InsertPost(post);

					rd = getServletContext().getRequestDispatcher("/postRecent.jsp");
				}

				else {
					rd = getServletContext().getRequestDispatcher("/http401.jsp");
				}
				break;

				//Submit edited blog
			case "/blog/blog/submit":
				if (checkLogin(request.getSession())) {
					System.out.println("Pretending to submit a blog");

					rd = getServletContext().getRequestDispatcher("/blogView.jsp");
				}

				else {
					rd = getServletContext().getRequestDispatcher("/http401.jsp");
				}
				break;

				//Submit edited profile
			case "/blog/profile/submit":
				if (checkLogin(request.getSession())) {
					System.out.println("Pretending to edit a profile");

					rd = getServletContext().getRequestDispatcher("/profileView.jsp");
				}

				else {
					String username = (String) request.getParameter("name");
					String motto = (String) request.getParameter("motto");
					String password = (String) request.getParameter("password");

					ProfileDTO profile = new ProfileDTO(0, username, motto, LocalDateTime.now());
					
					profileDB.InsertProfile(profile);
					authenticator.InsertPassword(username, password);
					
					boolean authenticated = authenticator.AuthenticateUser(username, password);
					
					if (authenticated)
					{
						request.getSession().setAttribute("loggedInUser", username);
						rd = getServletContext().getRequestDispatcher("/index.jsp");
					}
					rd = getServletContext().getRequestDispatcher("/profileView.jsp");
				}
				break;
				
				//Login
			case "/blog/login/submit":
				String username = (String) request.getParameter("username");
				String password = (String) request.getParameter("password");
				
				boolean authenticated = authenticator.AuthenticateUser(username, password);
				
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
				
    			//Logout
			case "/blog/logout":
				request.getSession().removeAttribute("loggedInUser");
				rd = getServletContext().getRequestDispatcher("/index.jsp");
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
    		
    		RequestDispatcher rd = getServletContext().getRequestDispatcher("/httperr.jsp");
    		
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
    			PostDTO post = postDB.getPostById(1).get(0);
    			
    			request.setAttribute("post", post);
    			rd = getServletContext().getRequestDispatcher("/postView.jsp");
    			break;
    			
    			//Write new post
			case "/blog/post/edit":
				if (checkLogin(request.getSession())) {
					rd = getServletContext().getRequestDispatcher("/postEdit.jsp");
				}

				else {
					rd = getServletContext().getRequestDispatcher("/http401.jsp");
				}
				break;
    			
    			
    			//View blog
    		case "/blog/blog":
    			rd = getServletContext().getRequestDispatcher("/blogView.jsp");
    			break;
    			
    			//Add new blog
			case "/blog/blog/edit":
				if (checkLogin(request.getSession())) {
					rd = getServletContext().getRequestDispatcher("/blogEdit.jsp");
				}

				else {
					rd = getServletContext().getRequestDispatcher("/http401.jsp");
				}
				break;
    			
    			
    			//View profile
    		case "/blog/profile":
    			ProfileDTO profile = profileDB.getProfileById(1).get(0);
    			
    			request.setAttribute("profile", profile);
    			rd = getServletContext().getRequestDispatcher("/profileView.jsp");
    			break;
    			
    			//Write new post
    		case "/blog/profile/edit":
					rd = getServletContext().getRequestDispatcher("/profileEdit.jsp");
    			break;
    			
    		default:
        		String rc = request.getContextPath();
        		String sc = getServletContext().getContextPath();
        		System.out.println("Default GET path" + rc + " | " + sc + " | " + url);
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

    protected boolean checkLogin(HttpSession session)
    {
    	boolean loggedIn = false;
    	
		if (session.getAttribute("loggedInUser") != null)
			{
				loggedIn = true;
			}
		
		return loggedIn;
    }
}