package nl.cerios.clog.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.cerios.clog.business.AppConfiguration;
import nl.cerios.clog.business.ClogLogic;
import nl.cerios.clog.domain.BlogDO;
import nl.cerios.clog.domain.PostDO;
import nl.cerios.clog.domain.ProfileDO;

import org.yaml.snakeyaml.Yaml;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.ValidationException;

public class ClogServlet2 extends HttpServlet{
	private static final long serialVersionUID = 1434964914372365428L;
	
	private ClogLogic business;
	
	public void init(ServletConfig config)
			   throws ServletException {
		super.init(config);
		
		try {
			Yaml yaml = new Yaml();
			File yamlConfig = new File(getServletContext().getRealPath("/config.yml"));
			InputStream input = new FileInputStream(yamlConfig);				
			AppConfiguration appConfig = yaml.loadAs(input,  AppConfiguration.class);
			input.close();
				
			business.init(appConfig);
		}
		catch(Exception e){
			System.err.println("Servlet initialization error");
		}
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)  
            throws ServletException, IOException {
		try{			
    		String url = request.getRequestURI().substring(request.getContextPath().length());
		
			switch (url) {
				case "/post/submit":		submitNewPost(request, response);		break; //Submit new post
				case "/blog/submit":												break; //Submit new blog	
				case "/profile/submit":		submitNewProfile(request, response);	break; //Submit new profile
				
				case "/post/resubmit":		submitEditedPost(request, response);	break; //Submit edited post
				case "/blog/resubmit":		submitEditedBlog(request, response);	break; //Submit edited blog	
				case "/profile/resubmit":	submitEditedProfile(request, response);	break; //Submit edited profile

	    		case "/post":				showPostById(request, response);		break; //View post?id=
	    		case "/blog":				showBlogById(request, response);		break; //View blog?id=
	    		case "/profile":			showProfileById(request, response);		break; //View profile?id=
	    		
				case "/login/submit":		submitLogin(request, response);			break; //Login
				case "/logout":			submitLogout(request, response);		break; //Logout
				
				default:
	        		String rc = request.getContextPath();
	        		System.out.println("Default POST " + rc + url);
	        		request.setAttribute("errordetails", "POST: How did you even?");
	        		showError500(request, response);
					break;
			}

		}
		catch (ServletException e) {
			System.err.println("Servlet POST " + e);
			request.setAttribute("errordetails", e.getMessage());
			showError500(request, response);
		}
	}

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	try{
    		String url = request.getRequestURI().substring(request.getContextPath().length());
    		
    		switch (url)
    		{
    		case "":				showIndex(request, response);			break;//Homepage
    		case "/index":			showIndex(request, response);			break;
    		case "/about":			showAbout(request, response);			break; //About
    		case "/login":			showLogin(request, response);			break; //Login
    		case "/recent":			showRecentPosts(request, response);		break; //Recent posts
    		
    		case "/post":			showPostById(request, response);		break; //View post?id=
    		case "/blog":			showBlogById(request, response);		break; //View blog?id=
    		case "/profile":		showProfileById(request, response);		break; //View profile?id=
    		
    		case "/blog/me":		showLoggedInBlog(request, response);	break; //View My Blog
    		case "/profile/me":		showLoggedInProfile(request, response);	break; //View My Profile
    		
			case "/post/new":		showNewPostForm(request, response);		break; //Write new post
    		case "/profile/new":	showNewProfileForm(request, response);  break; //Add new Profile
    		
			case "/blog/new":		showLoggedInBlog(request, response);	break; //Add new blog (Unneeded atm)
			
			case "/post/edit":		showEditPostForm(request, response);	break; //Edit post
    		case "/profile/edit":	showEditProfileForm(request, response); break; //Edit Profile
			case "/blog/edit":		showEditBlogForm(request, response);	break; //Edit blog
			
			case "/post/find":		showFindPostForm(request, response);	break; //Find post
			case "/blog/find":		showFindBlogForm(request, response);	break; //Find blog
			case "/profile/find":	showFindProfileForm(request, response);	break; //Find profile
			
    		default:
        		String rc = request.getContextPath();
        		System.out.println("Default GET: " + rc + url);
        	
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
    
    //Add new post [POST] [DB interaction, safeties]
    protected void submitNewPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (checkLoggedIn(request.getSession())) {
			int blogId = Integer.parseInt(request.getParameter("blogId"));
			String ptitle = (String) request.getParameter("title");
			String ptext = (String) request.getParameter("text");

			PostDO post = new PostDO(0, blogId, LocalDateTime.now(), ptitle, ptext);
			
			postDB.insertPost(cleanPost);
			showLoggedInBlog(request, response);
		}

		else {
			showError401(request, response);
		}
	}

    //Automatic for now, this is useless [evt. db interaction]
	protected void submitNewBlog(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//sanitize input
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
			ProfileDTO cleanProfile = sanitize(profile);

			profileDB.insertProfile(cleanProfile);
			authenticator.InsertPassword(username, password);

			int profileId = authenticator.GetIdByName(username);

			BlogDTO newBlog = new BlogDTO(0, profileId, "Blog " + username, username + "'s blog");
			BlogDTO cleanBlog = sanitize(newBlog);
			
			blogDB.insertBlog(cleanBlog);
			
			boolean authenticated = authenticator.AuthenticateUser(username, password);

			if (authenticated) {
				request.getSession().setAttribute("loggedInUser", username);
				showLoggedInProfile(request, response);
			}
		}
	}
	
	protected void submitEditedPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!checkLoggedIn(request.getSession())) {
			showError401(request, response);
		}

		else {
			int postId = Integer.parseInt(request.getParameter("postId"));
			int blogId = Integer.parseInt(request.getParameter("blogId"));
			LocalDateTime ptimestamp = LocalDateTime.parse(request.getParameter("timestamp"));
			String ptitle = (String) request.getParameter("title");
			String ptext = (String) request.getParameter("text");

			PostDTO post = new PostDTO(postId, blogId, ptimestamp, ptitle, ptext);
			PostDTO cleanPost = sanitize(post);
			
			request.setAttribute("post", cleanPost);
			
			postDB.updatePost(cleanPost);
			getServletContext().getRequestDispatcher("/postView.jsp").forward(request, response);
		}
	}
	
    //Automatic for now, this is useless [evt. db interaction]
	protected void submitEditedBlog(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//sanitize input
		if (!checkLoggedIn(request.getSession())) {
			showError401(request, response);
		}

		else {
			int blogId = Integer.parseInt(request.getParameter("blogId"));
			int profileId = Integer.parseInt(request.getParameter("profileId"));
			String blogTitle = (String) request.getParameter("title");
			String blogDescription = (String) request.getParameter("description");

			BlogDTO blog = new BlogDTO(blogId, profileId, blogTitle, blogDescription);
			BlogDTO cleanBlog = sanitize(blog);
			
			request.setAttribute("blog", cleanBlog);
			
			blogDB.updateBlog(cleanBlog);

			getServletContext().getRequestDispatcher("/blog?id=" + blog.getId()).forward(request, response);
		}
	}
	
    //[DB ACCESS PROFILE, BLOG]TODO
	protected void submitEditedProfile(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!checkLoggedIn(request.getSession())) {
			showError401(request, response);
		}

		else {
				int profileId = Integer.parseInt(request.getParameter("profileId"));
				String profileName = (String) request.getParameter("name");
				String profileMotto = (String) request.getParameter("motto");
				LocalDateTime profileJoinDate = LocalDateTime.parse(request.getParameter("joinDate"));
	
				ProfileDTO profile = new ProfileDTO(profileId, profileName, profileMotto, profileJoinDate);
				ProfileDTO cleanProfile = sanitize(profile);
					
				profileDB.updateProfile(cleanProfile);
				
				showLoggedInProfile(request, response);
			}
	}
	
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
    //showNewBlogForm(req, res)
	protected void showNewProfileForm(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		if (!checkLoggedIn(request.getSession())) {
			getServletContext().getRequestDispatcher("/profileNew.jsp").forward(request, response);
		}

		else {
			showLoggedInProfile(request, response);
			}
	}
	
	////Edit forms [Prepopulate form with DTO content]
	protected void showEditPostForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (checkLoggedIn(request.getSession())) {
			//Parse implicit ID from URL
			int postId = 0;
			
			try {
				postId = java.lang.Integer.parseInt(request.getParameter("id"));
				int profileId = getLoggedInId(request.getSession());
				PostDTO post = postDB.getPostById(postId);
				int postOwner = blogDB.getProfileByBlogId(post.getBlogId());
				
				//Make sure we own the post
				if (postOwner == getLoggedInId(request.getSession()))
				{
					request.setAttribute("profileId", profileId);
					request.setAttribute("post", post);
				
					getServletContext().getRequestDispatcher("/postEdit.jsp").forward(request, response);
				}
				
				else showError401(request, response);
			}
			catch (NumberFormatException e) {
				request.setAttribute("errordetails", "Malformed Post ID!");
				getServletContext().getRequestDispatcher("/http500.jsp").forward(request, response);
			}
		}

		else {
			showError401(request, response);
		}
	}
	protected void showEditProfileForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (checkLoggedIn(request.getSession())) {
			int profileId = getLoggedInId(request.getSession());
			ProfileDTO profile = profileDB.getProfileById(profileId);
			request.setAttribute("profile", profile);
			getServletContext().getRequestDispatcher("/profileEdit.jsp").forward(request, response);
		}
		
		else{
			showError404(request, response);
		}
	}
	protected void showEditBlogForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (checkLoggedIn(request.getSession())) {
			int profileId = getLoggedInId(request.getSession());
			BlogDTO blog = blogDB.getBlogByProfileId(profileId).get(0);
			request.setAttribute("blog", blog);
			getServletContext().getRequestDispatcher("/blogEdit.jsp").forward(request, response);
		}
		
		else{
			showError401(request, response);
		}
	}
	
	protected void showFindPostForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/postFind.jsp").forward(request, response);
	}
	
	protected void showFindBlogForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/blogFind.jsp").forward(request, response);
	}
	
	protected void showFindProfileForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/profileFind.jsp").forward(request, response);
	}
	
    
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
		List<PostDTO> cleanPosts = sanitize(posts);		
		request.setAttribute("posts", cleanPosts);

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
			request.setAttribute("errordetails", "Malformed Post ID: " + postId);
			getServletContext().getRequestDispatcher("/http500.jsp").forward(request, response);
			return;
		}

		//If the ID looks good, retrieve that post
		if (postId > 0)
		{	
			PostDTO post = postDB.getPostById(postId);
			
			//If the post is good, punch it into the request
			if (post != null)
			{
				PostDTO cleanPost = sanitize(post);
				request.setAttribute("post", cleanPost);

				getServletContext().getRequestDispatcher("/postView.jsp").forward(request, response);
			}
			
			//If we haven't put a post in the request, there's been a problem
			if (request.getAttribute("post") == null)
			{
				request.setAttribute("errordetails", "Post with ID " + postId + " doesn't exist.");
				getServletContext().getRequestDispatcher("/http500.jsp").forward(request, response);
			}
		}
		
		else
		 {
			request.setAttribute("errordetails", "Malformed Post ID: " + postId);
			getServletContext().getRequestDispatcher("/http500.jsp").forward(request, response);
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
			request.setAttribute("errordetails", "Malformed Blog ID: " + blogId);		
			getServletContext().getRequestDispatcher("/http500.jsp").forward(request, response);
			return;
		}

		//If the ID looks good, retrieve that blog
		if (blogId > 0)
		{	
			BlogDTO blog = blogDB.getBlogById(blogId);
			
			//If the blog is good, add it to the request and try grab some posts
			if (blog != null)
			{
				BlogDTO cleanBlog = sanitize(blog);
				request.setAttribute("blog", cleanBlog);
				
				List<PostDTO> blogPosts = postDB.getPostByBlogId(blogId);
				List<PostDTO> cleanPosts = sanitize(blogPosts);
				request.setAttribute("posts", cleanPosts);
				
				getServletContext().getRequestDispatcher("/blogView.jsp").forward(request, response);
			}
			
			//If we haven't put a post in the request, there's been a problem
			if (request.getAttribute("blog") == null)
			{
				request.setAttribute("errordetails", "Blog with ID " + blogId + " doesn't exist.");
				getServletContext().getRequestDispatcher("/http500.jsp").forward(request, response);
			}
		}
		
		else  {
			request.setAttribute("errordetails", "Malformed Blog ID: " + blogId);
			getServletContext().getRequestDispatcher("/http500.jsp").forward(request, response);
		}
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
    			return;
    		}
    		
    		//If the ID looks good, retrieve that profile
    		if (profileId != 0)
    		{	
    			ProfileDTO profile = profileDB.getProfileById(profileId);
    			
    			//If the profile is good, add it to the request
    			if (profile != null)
    			{
    				ProfileDTO cleanProfile = sanitize(profile);
    				request.setAttribute("profile", cleanProfile);
    				
    				try{
    					BlogDTO cleanBlog = sanitize(blogDB.getBlogByProfileId(cleanProfile.getId()).get(0));
    					request.setAttribute("blog", cleanBlog);
    				}
    				
    				catch (Exception e)
    				{
    					System.out.println(e.getMessage());
    				}
    				
    				
    				getServletContext().getRequestDispatcher("/profileView.jsp").forward(request, response);
    			}
    			
    			//If we haven't put a profile in the request, there's been a problem
    			if (request.getAttribute("profile") == null)
    			{
    				request.setAttribute("errordetails", "Profile with ID " + profileId + " doesn't exist.");
    				getServletContext().getRequestDispatcher("/http500.jsp").forward(request, response);
    			}
    		}
    		
    		else {
    			request.setAttribute("errordetails", "Malformed Profile ID: " + profileId);
    			getServletContext().getRequestDispatcher("/http500.jsp").forward(request, response);
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
					ProfileDTO cleanProfile = sanitize(profile);
					request.setAttribute("profile", cleanProfile);
					try{
    					BlogDTO cleanBlog = sanitize(blogDB.getBlogByProfileId(cleanProfile.getId()).get(0));
    					request.setAttribute("blog", cleanBlog);
    				}
    				
    				catch (Exception e)
    				{
    					System.out.println(e.getMessage());
    				}
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
    			BlogDTO	cleanBlog = sanitize(blog);
    			request.setAttribute("blog", cleanBlog);
    			
				List<PostDTO> blogPosts = postDB.getPostByBlogId(blog.getId());
				List<PostDTO> cleanPosts = sanitize(blogPosts);				
				request.setAttribute("posts", cleanPosts);
				
				getServletContext().getRequestDispatcher("/blogView.jsp").forward(request, response);
    		}
    	}
    	
    	else showError401(request, response);
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