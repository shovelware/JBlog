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
import nl.cerios.blog.database.BlogDAOSQL;
import nl.cerios.blog.database.BlogDTO;
import nl.cerios.blog.database.ConnectionFactory;
import nl.cerios.blog.database.PostDAOSQL;
import nl.cerios.blog.database.PostDTO;
import nl.cerios.blog.database.ProfileDAOSQL;
import nl.cerios.blog.database.ProfileDTO;

public class BlogServlet extends HttpServlet{
	private static final long serialVersionUID = -73186648007060644L;
	
	private PostDAOSQL postDB = new PostDAOSQL();
	private ProfileDAOSQL profileDB = new ProfileDAOSQL();
	private BlogDAOSQL blogDB = new BlogDAOSQL();
	
	private Authenticator authenticator = new Authenticator();
	
	private int postCount = 5;
	
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
					
					request.getSession().setAttribute("loggedInUser", authenticator.GetIdByName(username));
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
			request.setAttribute("errordetails", e.getMessage());
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
    			showIndex(request, response);
    			break;
    		case "/blog/index":
    			showIndex(request, response);
    			break;
    			
    			//About
    		case "/blog/about":
    			showAbout(request, response);
    			break;
    			
    			//Login
    		case "/blog/login":
    			showLogin(request, response);
    			break;
    			
    			//Recent posts
    		case "/blog/recent":
    			showRecentPosts(request, response);
    			break;
    			
    			//View post
    		case "/blog/post":
    			showPostById(request, response);
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
    			showBlogById(request, response);
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
    			showProfileById(request, response);
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
    		
    		try{
    			rd.forward(request, response);
    		}
    		catch(IllegalStateException e)
    		{
    			System.err.println("HURK" + e.getMessage());
    			//Temporary while refactoring
    		}
    	}
    	catch (ServletException e){
    		System.err.println("Servlet GET " + e);
    		request.setAttribute("errordetails", e.getMessage());
    		response.sendRedirect("httperr.jsp");
    	}
    	
    }

    //Comments:
    //SHOW will attempt to redirect to a new HTML page, failing that it will go to error
    //SUBMIT will validate data and return status/id
    //Every request should end in a SHOW, nothing should come after SHOW
    
    //Add new stuff [POST] [DB interaction, safeties]
    //submitNewPost(req, res)postDTO pull this from request [and id from url?]
    //submitNewBlog(req, res)blogDTO
    //submitNewProfile(req, res)profileDTO
    
    //Update edited stuff [POST] [DB interaction, safeties]
    //submitEditedPost(req, res, postDTO)
    //submitEditedBlog(req, res, blogDTO)
    //submitEditedProfile(req, res, profileDTO)
    
    //Auth [POST]
    //submitLogin(req, res)
    //submitLogout(req, res)
    
    //Admin stuff [POST]
    //AdminSessionEnable(res, req)
    //AdminSessionDisable(res, req)
    
    //New forms
    //showNewPostForm(req, res)
    //showNewBlogForm(req, res)
    //showNewProfileForm(req, res)
    
    //Edit forms [Prepopulate form with DTO content]
    //showEditPostForm(req, res, postId)   
    //showEditBlogForm(req, res, blogId)
    //showEditProfileForm(req, res, profileId)
    
    //Generic pages
    //Show Index
    protected void showIndex(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.jsp");
    	rd.forward(request, response);
    }

	// Show about page
	protected void showAbout(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/about.jsp").forward(request, response);
	}

	// Show login page
	protected void showLogin(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/login.jsp").forward(request, response);
	}

	// Show recent posts
	protected void showRecentPosts(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		List<PostDTO> posts = postDB.getPostByTimestamp(LocalDateTime.now(), postCount);
		request.setAttribute("posts", posts);

		getServletContext().getRequestDispatcher("/postRecent.jsp").forward(request, response);
	}

	// Content pages
	//Show Post by ID [PARSES URI]
	protected void showPostById(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//Parse implicit ID from URL
		int postId = 0;
		
		try {
			postId = java.lang.Integer.parseInt(request.getParameter("id"));
		}

		catch (NumberFormatException e) {
			request.setAttribute("errordetails", "Malformed Post ID!");		
			getServletContext().getRequestDispatcher("/http500.jsp").forward(request, response);
		}

		//If the ID looks good, retreive that post
		if (postId != 0)
		{	
			PostDTO post = postDB.getPostById(postId);
			
			//If the post is good, punch it into the request
			if (post != null)
			{
				request.setAttribute("post", post);
				getServletContext().getRequestDispatcher("/postView.jsp").forward(request, response);
			}
			
			//If we haven't put a post in the request, there's been a problem
			if (request.getAttribute("post") == null)
			{
				request.setAttribute("errordetails", "Post with that ID doesn't exist :(");
				getServletContext().getRequestDispatcher("/http500.jsp").forward(request, response);
			}
		}
	}
	
	//Show Blog by ID [PARSES URI]
    protected void showBlogById(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
    	//Parse implicit ID from URL
		int blogId = 0;
		
		try {
			blogId = java.lang.Integer.parseInt(request.getParameter("id"));
		}

		catch (NumberFormatException e) {
			System.out.println(e);
			request.setAttribute("errordetails", "Malformed Blog ID!");		
			getServletContext().getRequestDispatcher("/http500.jsp").forward(request, response);
		}

		//If the ID looks good, retreive that blog
		if (blogId != 0)
		{	
			BlogDTO blog = blogDB.getBlogById(blogId);
			
			//If the blog is good, add it to the request and try grab some posts
			if (blog != null)
			{
				request.setAttribute("blog", blog);
				
				List<PostDTO> blogPosts = postDB.getPostByBlogId(blogId);
				request.setAttribute("posts", blogPosts);
				
				getServletContext().getRequestDispatcher("/blogView.jsp").forward(request, response);
			}
			
			//If we haven't put a post in the request, there's been a problem
			if (request.getAttribute("blog") == null)
			{
				request.setAttribute("errordetails", "Unable to retreive a Blog with that ID :(");
				getServletContext().getRequestDispatcher("/http500.jsp").forward(request, response);
			}
		}
    }
	
    //Show Profile by ID [PARSES URI]
    protected void showProfileById(HttpServletRequest request, HttpServletResponse response)
    	throws ServletException, IOException {
    		
    		//Parse implicit ID from URI
    		int profileId = 0;
    		
    		try {
    			profileId = java.lang.Integer.parseInt(request.getParameter("id"));
    		}

    		catch (NumberFormatException e) {
    			request.setAttribute("errordetails", "Malformed Profile ID!");		
    			getServletContext().getRequestDispatcher("/http500.jsp").forward(request, response);
    		}

    		//If the ID looks good, retreive that profile
    		if (profileId != 0)
    		{	
    			ProfileDTO profile = profileDB.getProfileById(profileId);
    			
    			//If the post is good, punch it into the request
    			if (profile != null)
    			{
    				request.setAttribute("profile", profile);
    				getServletContext().getRequestDispatcher("/profileView.jsp").forward(request, response);
    			}
    			
    			//If we haven't put a profile in the request, there's been a problem
    			if (request.getAttribute("profile") == null)
    			{
    				request.setAttribute("errordetails", "Profile with that ID doesn't exist :(");
    				getServletContext().getRequestDispatcher("/http500.jsp").forward(request, response);
    			}
    		}
    }
    
    //showMyProfile(req, res) //Shows logged in profile or error
    //showMyBlog(req, res) //Shows logged in blog or error
    
    //Gets the ID of the currently logged in user, or 0 if nobody's logged in
    protected int getLoggedInID(HttpSession session)
    {
    	int id = 0;
    	
    	Object potentialId = session.getAttribute("loggedInUser");
    	
    	if (potentialId != null)
    	{
    		id = (int)potentialId;
    	}
    	return id;
    	
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
    
    protected boolean checkAdmin(HttpSession session)
    {
    	boolean admin = false;
    	
    	if (session.getAttribute("admin") != null)
    	{
    		admin = true;
    	}
    	
    	return admin;
    }
}