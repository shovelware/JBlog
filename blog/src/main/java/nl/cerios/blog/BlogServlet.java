package nl.cerios.blog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ShutdownChannelGroupException;
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
		
		try {
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
		
			switch (url) {
				case "/blog/post/submit":		submitNewPost(request, response);		break; //Submit edited post
				case "/blog/blog/submit":												break; //Submit edited blog	
				case "/blog/profile/submit":	submitNewProfile(request, response);	break; //Submit edited profile
				case "/blog/login/submit":		submitLogin(request, response);			break; //Login
				case "/blog/logout":			submitLogout(request, response);		break; //Logout
				
				default:
	        		String rc = request.getContextPath();
	        		String sc = getServletContext().getContextPath();
	        		System.out.println("Default GET path" + rc + " | " + sc + " | " + url);
	        		request.setAttribute("errordetails", "POST: How did you even?");
	    			getServletContext().getRequestDispatcher("/http500.jsp").forward(request, response);        
					break;
			}

		}

		catch (Exception e) {
			System.err.println("Servlet POST " + e);
			request.setAttribute("errordetails", e.getMessage());
			showError500(request, response);
		}
	}

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	try{
    		String url = request.getRequestURI();
    		
    		RequestDispatcher rd = getServletContext().getRequestDispatcher("/httperr.jsp");
    		
    		switch (url)
    		{
    		case "/blog/":				showIndex(request, response);			break;//Homepage
    		case "/blog/index":			showIndex(request, response);			break;
    		case "/blog/about":			showAbout(request, response);			break; //About
    		case "/blog/login":			showLogin(request, response);			break; //Login
    		case "/blog/recent":		showRecentPosts(request, response);		break; //Recent posts
    		case "/blog/post":			showPostById(request, response);		break; //View post?id=
    		case "/blog/blog":			showBlogById(request, response);		break; //View blog?id=
    		case "/blog/profile":		showProfileById(request, response);		break; //View profile?id=
    		case "/blog/blog/me":		showLoggedInBlog(request, response);	break; //View My Blog
    		case "/blog/profile/me":	showLoggedInProfile(request, response);	break; //View My Profile
			case "/blog/post/new":		showNewPostForm(request, response);		break; //Write new post
    		case "/blog/profile/new":	showNewProfileForm(request, response);  break; //Add new Profile
			case "/blog/blog/new":		showLoggedInBlog(request, response);	break; //Add new blog (Unneeded atm)
    		default:
        		String rc = request.getContextPath();
        		String sc = getServletContext().getContextPath();
        		System.out.println("Default GET path" + rc + " | " + sc + " | " + url);
        	
        		request.setAttribute("errordetails", "GET: How did you even?");
    			showError500(request, response);;        		
    			break;
    		}
    		
    	}
    	catch (ServletException e){
    		System.err.println("Servlet GET " + e);
    		request.setAttribute("errordetails", e.getMessage());
    		showError500(request, response);
    	}
    	
    }

    //Comments:
    //GET ~~ SHOW: will attempt to redirect to a new HTML page, failing that it will go to error
    //POST ~~ SUBMIT: will validate data and then SHOW a page based on result
    
    //Add new stuff [POST] [DB interaction, safeties]
	protected void submitNewPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (checkLoggedIn(request.getSession())) {
			int blogId = Integer.parseInt(request.getParameter("blogId"));
			String ptitle = (String) request.getParameter("title");
			String ptext = (String) request.getParameter("text");

			PostDTO post = new PostDTO(0, blogId, LocalDateTime.now(), ptitle, ptext);
			postDB.InsertPost(post);

			getServletContext().getRequestDispatcher("/postRecent.jsp").forward(request, response);

		}

		else {
			showError401(request, response);
		}
	}

    //Automatic for now, this is uesless
	protected void submitNewBlog(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
	}
	
    //[DB ACCESS PROFILE, AUTH, BLOG]
	protected void submitNewProfile(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (checkLoggedIn(request.getSession())) {
			showLoggedInProfile(request, response);
		}

		else {
			String username = (String) request.getParameter("name");
			String motto = (String) request.getParameter("motto");
			String password = (String) request.getParameter("password");

			ProfileDTO profile = new ProfileDTO(0, username, motto, LocalDateTime.now());

			profileDB.InsertProfile(profile);
			authenticator.InsertPassword(username, password);

			int profileId = authenticator.GetIdByName(username);

			BlogDTO newBlog = new BlogDTO(0, profileId, "", "");
			
			blogDB.InsertBlog(newBlog);
			
			boolean authenticated = authenticator.AuthenticateUser(username, password);

			if (authenticated) {
				request.getSession().setAttribute("loggedInUser", username);
				showLoggedInProfile(request, response);
			}
		}
	}
	
    //Update edited stuff [POST] [DB interaction, safeties]
    //submitEditedPost(req, res, postDTO)
    //submitEditedBlog(req, res, blogDTO)
    //submitEditedProfile(req, res, profileDTO)
    
    //Auth [POST]
	protected void submitLogin(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = (String) request.getParameter("username");
		String password = (String) request.getParameter("password");

		boolean authenticated = authenticator.AuthenticateUser(username, password);

		if (authenticated) {
			request.getSession().setAttribute("loggedInUser", username);
			showLoggedInProfile(request, response);
		}

		else {
			request.setAttribute("errorMessage", "Login Failed, please try again");
			showLogin(request, response);
		}
	}
	
	protected void submitLogout(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getSession().removeAttribute("loggedInUser");
		showIndex(request, response);
	}
    
    //Admin stuff [POST]
    //AdminSessionEnable(res, req)
    //AdminSessionDisable(res, req)
    
    ////New forms
	protected void showNewPostForm(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		if (checkLoggedIn(request.getSession())) {
			
			int profileId = getLoggedInId(request.getSession());
			int blogId = blogDB.getBlogByProfileId(profileId).get(0).getId();
			
			request.setAttribute("profileId", profileId);
			request.setAttribute("blogId", blogId);
			
			getServletContext().getRequestDispatcher("/postNew.jsp").forward(request, response);
		}

		else {
			showError401(request, response);
		}
	}
	protected void showNewProfileForm(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		if (!checkLoggedIn(request.getSession())) {
			getServletContext().getRequestDispatcher("/profileNew.jsp").forward(request, response);
		}

		else {
			showLoggedInProfile(request, response);
			}
	}
    //showNewBlogForm(req, res)
    
    ////Edit forms [Prepopulate form with DTO content]
    //showEditPostForm(req, res, postId)   
    //showEditBlogForm(req, res, blogId)
    //showEditProfileForm(req, res, profileId)
    
    //Generic pages
	protected void showIndex(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
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

	//// Content pages
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

		//If the ID looks good, retrieve that post
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

		//If the ID looks good, retrieve that blog
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
				request.setAttribute("errordetails", "Unable to retrieve a Blog with that ID :(");
				getServletContext().getRequestDispatcher("/http500.jsp").forward(request, response);
			}
		}
    }

    //Errors
    protected void showError401(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/http401.jsp").forward(request, response);
    }
    protected void showError403(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/http403.jsp").forward(request, response);
    } 
    protected void showError404(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/http404.jsp").forward(request, response);
    }
    protected void showError500(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/http500.jsp").forward(request, response);
    }
    
    //[PARSES URI] Show Profile by ID
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

    		//If the ID looks good, retrieve that profile
    		if (profileId != 0)
    		{	
    			ProfileDTO profile = profileDB.getProfileById(profileId);
    			
    			//If the profile is good, add it to the request
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
    
    //[PARSES SESSION] //Shows logged in profile or error
    protected void showLoggedInProfile(HttpServletRequest request, HttpServletResponse response)
        	throws ServletException, IOException {
    	
    	//First we have to be logged in
    	if (checkLoggedIn(request.getSession()))
    	{
			//Get the ID of the logged in Profile
			int	profileId = getLoggedInId(request.getSession());
	
			//If the ID looks good, retrieve that profile
			if (profileId != 0)
			{	
				ProfileDTO profile = profileDB.getProfileById(profileId);
				
				//If the profile is good, punch it into the request
				if (profile != null)
				{
					request.setAttribute("profile", profile);
					getServletContext().getRequestDispatcher("/profileView.jsp").forward(request, response);
				}
				
				//If we haven't put a profile in the request, there's been a problem
				if (request.getAttribute("profile") == null)
				{
					request.setAttribute("errordetails", "Couldn't retrieve profile :(");
					showError500(request, response);
				}
			}
    	}
    	
    	else showError401(request, response);
    }
    
    //showMyBlog(req, res) //Shows logged in blog or error [NEEDED?]
    protected void showLoggedInBlog(HttpServletRequest request, HttpServletResponse response)
        	throws ServletException, IOException {
    	
    	//Check if we're logged in
    	if (checkLoggedIn(request.getSession()))
    	{
    		int profileId = getLoggedInId(request.getSession());
    		ProfileDTO profile = profileDB.getProfileById(profileId);
    		BlogDTO blog = blogDB.getBlogByProfileId(profileId).get(0);
    		
    		if (blog != null && profile != null)
    		{
    			request.setAttribute("blog", blog);
    			
				List<PostDTO> blogPosts = postDB.getPostByBlogId(blog.getId());
				request.setAttribute("posts", blogPosts);
				
				getServletContext().getRequestDispatcher("/blogView.jsp").forward(request, response);
    		}
    	}
    	
    	else showError401(request, response);
    	
    }
    
    //[PARSES SESSION] Gets the ID of the currently logged in user, or 0 if nobody's logged in
    protected int getLoggedInId(HttpSession session)
    {
    	int id = 0;
    	
    	Object potentialId = authenticator.GetIdByName((String)session.getAttribute("loggedInUser"));
    	//Dunno about using the auth class for this?
    	
    	if (potentialId != null)
    	{
    		id = (int)potentialId;
    	}
    	return id;
    }

    //[PARSES SESSION] Returns true if logged in
    protected boolean checkLoggedIn(HttpSession session)
    {
    	boolean loggedIn = false;
    	
		if (session.getAttribute("loggedInUser") != null)
			{
				loggedIn = true;
			}
		
		return loggedIn;
    }
    
    //[PARSES SESSION]
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