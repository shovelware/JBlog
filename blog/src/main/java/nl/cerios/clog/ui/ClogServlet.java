package nl.cerios.clog.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.cerios.clog.business.AppConfiguration;
import nl.cerios.clog.business.ClogLogic;
import nl.cerios.clog.exception.AuthenticationException;
import nl.cerios.clog.exception.InvalidInputException;
import nl.cerios.clog.exception.ProcessingException;
import nl.cerios.clog.object.BlogDTO;
import nl.cerios.clog.object.PostDTO;
import nl.cerios.clog.object.ProfileDTO;

import org.yaml.snakeyaml.Yaml;

public class ClogServlet extends HttpServlet{
	private static final long serialVersionUID = 1434964914372365428L;
	
	private ClogLogic business = new ClogLogic();
	
	private final int MAGICPOSTNUMBER = 10;
	
	private enum ErrorCode {
		AUTHENTICATION(401),
		FORBIDDEN(403),
		NOTFOUND(404),
		SERVER(500);
		
		private final int errorCode;
		
		ErrorCode(int errorCode)
		{
			this.errorCode = errorCode;
		}
	}
	
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
				case "/post/submit":		submitNewPost(request, response);		break; //Submit new DO post
				case "/blog/submit":		submitNewBlog(request, response);		break; //Submit new DO blog	
				case "/profile/submit":		submitNewProfile(request, response);	break; //Submit new DO profile
				
				case "/post/resubmit":		submitUpdatedPost(request, response);	break; //Submit updated DO post
				case "/blog/resubmit":		submitUpdatedBlog(request, response);	break; //Submit updated DO blog	
				case "/profile/resubmit":	submitUpdatedProfile(request, response);break; //Submit updated DO profile

	    		case "/post":				showPostById(request, response);		break; //View post?id=
	    		case "/blog":				showBlogById(request, response);		break; //View blog?id=
	    		case "/profile":			showProfileById(request, response);		break; //View profile?id=
	    		
				case "/login/submit":		submitLogin(request, response);			break; //Login
				case "/logout":				submitLogout(request, response);		break; //Logout
				
				case "/password/submit":	submitPassword(request, response);		break; //Submit new password
				
				case "/test/submit":		submitTestPage(request, response);		break;
				
				default:
	        		String rc = request.getContextPath();
	        		System.out.println("Default POST " + rc + url);
	        		
	        		request.setAttribute("errordetails", "POST: How did you even?");
	        		showError(request, response, ErrorCode.SERVER);
					break;
			}

		}
		catch (ServletException e) {
			System.err.println("Servlet POST " + e);
			request.setAttribute("errordetails", e.getMessage());
			showError(request, response, ErrorCode.SERVER);
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
    		
    		case "/profile/me":		showLoggedInProfile(request, response);	break; //View My Profile
    		
			case "/post/new":		showNewPostForm(request, response);		break; //Write new post
    		case "/profile/new":	showNewProfileForm(request, response);  break; //Add new Profile
			case "/blog/new":		showNewBlogForm(request, response);		break; //Add new blog
			
			case "/post/edit":		showEditPostForm(request, response);	break; //Edit post
    		case "/profile/edit":	showEditProfileForm(request, response); break; //Edit Profile
			case "/blog/edit":		showEditBlogForm(request, response);	break; //Edit blog
			
			case "/post/find":		showFindPostForm(request, response);	break; //Find post
			case "/blog/find":		showFindBlogForm(request, response);	break; //Find blog
			case "/profile/find":	showFindProfileForm(request, response);	break; //Find profile
			
			case "/password":		showPasswordForm(request, response);	break; //Reset password
			
			case "/test":			showTestPage(request, response);		break;//Test page
			
    		default:
        		String rc = request.getContextPath();
        		System.out.println("Default GET: " + rc + url);
        	
        		request.setAttribute("errordetails", "GET: How did you even?");
    			showError(request, response, ErrorCode.NOTFOUND);        		
    			break;
    		}
    		return;
    		
    	}
    	catch (ServletException e){
    		System.err.println("Servlet GET " + e);
    		request.setAttribute("errordetails", e.getMessage());
    		showError(request, response, ErrorCode.SERVER);
    		return;
    	}
    	
    }

    //Comments:
    //GET ~~ SHOW: will attempt to redirect to a new HTML page, failing that it will go to error
    //POST ~~ SUBMIT: will validate data and then SHOW a page based on result
    
    //General function layout:
    //Data needed for lookup/Authentication checks
    //Page Variables = null
    //[try Database access][catch error]
    //Populate request
    //Successful operation request forward
    
    protected void submitTestPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
    	
    		request.setAttribute("testString", request.getAttribute("testInput"));
    		request.setAttribute("testString1", request.getAttribute("testInput1"));
    		request.setAttribute("testString2", request.getAttribute("testInput2"));
    		request.setAttribute("testString3", request.getAttribute("testInput3"));
    		request.setAttribute("testString4", request.getAttribute("testInput4"));
    		request.setAttribute("testString5", request.getAttribute("testInput5"));
    		request.setAttribute("testString6", request.getAttribute("testInput6"));
    		request.setAttribute("testString7", request.getAttribute("testInput7"));
    		request.setAttribute("testString8", request.getAttribute("testInput8"));
    		request.setAttribute("testString9", request.getAttribute("testInput9"));
    		
    	request.getRequestDispatcher("/test.jsp").forward(request, response);
    }
    
    protected void showTestPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
    	request.getRequestDispatcher("/test.jsp").forward(request, response);
    }
    
    //Add new post [POST] [DB interaction, safeties]
    protected void submitNewPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (checkLoggedIn(request.getSession()) == false) {
			showError(request, response, ErrorCode.AUTHENTICATION, "Error submitting post: User not logged in!");
			return;
			//Maybe redirect to edit post form or sth?
		}
		
		int blogId = Integer.parseInt(request.getParameter("blogId"));
		String ptitle = (String) request.getParameter("title");
		String ptext = (String) request.getParameter("text");

		PostDTO post = new PostDTO(0, blogId, getLoggedInId(request.getSession()), LocalDateTime.now(), ptitle, ptext);
		
		int newId = 0;
		
		try {
			newId = business.insertPost(post, getLoggedInProfile(request.getSession()));
		} 
		catch (AuthenticationException e) {
			showError(request, response, ErrorCode.AUTHENTICATION, e.getMessage());
			return;
    	} 
		catch (InvalidInputException | ProcessingException e) {
    		showError(request, response, ErrorCode.SERVER, e.getMessage());
    		return;
		}
		
		if (newId != 0) {
			showPost(request, response, newId);
			return;
		}
		
		else {
			showLoggedInProfile(request, response);
			return;
		}
	}
    
    //Add new blog [POST]
	protected void submitNewBlog(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (checkLoggedIn(request.getSession()) == false) {
				showError(request, response, ErrorCode.AUTHENTICATION, "Error submitting blog: User not logged in!");
				return;
				//Maybe redirect to edit blog form?
			}
			
		String btitle = (String) request.getParameter("title");
		String bdesc = (String) request.getParameter("description");

		BlogDTO blog = new BlogDTO(0, getLoggedInId(request.getSession()), btitle, bdesc);
		
		int newId = 0;
		
		try {
			newId = business.insertBlog(blog, getLoggedInProfile(request.getSession()));
		} 
		catch (AuthenticationException e) {
			showError(request, response, ErrorCode.AUTHENTICATION, e.getMessage());
			return;
    	} 
		catch (InvalidInputException | ProcessingException e) {
    		showError(request, response, ErrorCode.SERVER, e.getMessage());
    		return;
		}
		
		if (newId != 0) {
			showBlog(request, response, newId);
			return;
		}
		
		else {
			showLoggedInProfile(request, response);
			return;
		}
	}
	
    //[DB ACCESS PROFILE, AUTH, BLOG]
	protected void submitNewProfile(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (checkLoggedIn(request.getSession()) == true) {
			showLoggedInProfile(request, response);
			return;
		}

		String username = (String) request.getParameter("name");
		String motto = (String) request.getParameter("motto");
		String password = (String) request.getParameter("password");

		ProfileDTO profile = new ProfileDTO(0, username, motto, LocalDateTime.now());
		int newProfileId = 0;
		boolean authenticated = false;
		
		try {
			newProfileId = business.insertProfile(profile,  password);
			profile = new ProfileDTO(newProfileId, profile.getName(), profile.getMotto(), profile.getJoinDate());
			BlogDTO newBlog = new BlogDTO(0, newProfileId, "Blog " + username, username + "'s blog");
			business.insertBlog(newBlog, profile);
			authenticated = business.authenticateUser(username, password);
		}
		catch (InvalidInputException | ProcessingException | AuthenticationException e) {
			showError(request, response, ErrorCode.SERVER, e.getMessage());
			return;
		}

		if (authenticated) {
			request.getSession().setAttribute("loggedInUser", profile);
			showLoggedInProfile(request, response);
		}
		
		else showError(request, response, ErrorCode.SERVER, "Something went wrong. :(");
	}
	
	//TODO: Remove
	protected void submitUpdatedBlog(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (checkLoggedIn(request.getSession()) == false) {
			showError(request, response, ErrorCode.AUTHENTICATION);
		}

		int blogId = Integer.parseInt(request.getParameter("blogId"));
		int profileId = Integer.parseInt(request.getParameter("profileId"));
		String blogTitle = (String) request.getParameter("title");
		String blogDescription = (String) request.getParameter("description");

		BlogDTO updatedBlog = new BlogDTO(blogId, profileId, blogTitle, blogDescription);
		
		try {
			business.updateBlog(updatedBlog, getLoggedInProfile(request.getSession()));
		}
		catch (AuthenticationException e) {
			showError(request, response, ErrorCode.AUTHENTICATION, e.getMessage());
			return;
		}
		catch (InvalidInputException e) {
			showError(request, response, ErrorCode.NOTFOUND, e.getMessage());
			return;
		}
		
		showBlog(request, response, blogId);
		return;
	}
	
	protected void submitUpdatedProfile(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		showError(request, response, ErrorCode.NOTFOUND);
	}
	
	protected void submitUpdatedPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (checkLoggedIn(request.getSession()) == false) {
			showError(request, response, ErrorCode.AUTHENTICATION);
		}

		int postId = Integer.parseInt(request.getParameter("postId"));
		int blogId = Integer.parseInt(request.getParameter("blogId"));
		LocalDateTime ptimestamp = LocalDateTime.parse(request.getParameter("timestamp"));
		String ptitle = (String) request.getParameter("title");
		String ptext = (String) request.getParameter("text");

		PostDTO updatedPost = new PostDTO(postId, blogId, getLoggedInId(request.getSession()), ptimestamp, ptitle, ptext);
		
		try {
			business.updatePost(updatedPost, getLoggedInProfile(request.getSession()));
		}
		catch (AuthenticationException e) {
			showError(request, response, ErrorCode.AUTHENTICATION, e.getMessage());
			return;
		}
		catch (InvalidInputException e) {
			showError(request, response, ErrorCode.NOTFOUND, e.getMessage());
			return;
		}
		catch (ProcessingException e) {
			showError(request, response, ErrorCode.SERVER, e.getMessage());
			return;
		}
		
		showPost(request, response, postId);
		return;
	}
	
    //Automatic for now, this is useless [evt. db interaction]
/*	protected void submitEditedBlog(HttpServletRequest request, HttpServletResponse response)
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
	
    //[DB ACCESS PROFILE, BLOG]
	protected void submitEditedProfile(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (checkLoggedIn(request.getSession()) == false) {
			showError(request, response, ErrorCode.AUTHENTICATION);
			return;
		}

			int profileId = Integer.parseInt(request.getParameter("profileId"));
			String profileName = (String) request.getParameter("name");
			String profileMotto = (String) request.getParameter("motto");
			LocalDateTime profileJoinDate = LocalDateTime.parse(request.getParameter("joinDate"));
	
			ProfileDO profile = new ProfileDO(profileId, profileName, profileMotto, profileJoinDate);
			ProfileDO user = getLoggedInProfile(request.getSession());
			
			try {
				business.updateProfile(profile, user);
			}
			catch(ProcessingException e)
			{
				
			}
			showLoggedInProfile(request, response);
			return;
	}*/
	
    //Auth [POST]
	protected void submitLogin(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = (String) request.getParameter("username");
		String password = (String) request.getParameter("password");
		
		boolean authenticated = false;
		ProfileDTO user = null;
		try {
			user = business.getProfile(username);
			authenticated = business.authenticateUser(username, password);
		}
		catch(InvalidInputException | ProcessingException e)
		{
			request.setAttribute("errorMessage", "Login Failed, please try again");
			showLogin(request, response);
			return;
		}

		if (authenticated) {
			request.getSession().setAttribute("loggedInUser", user);
			showLoggedInProfile(request, response);
			return;
		}

		else {
			request.setAttribute("errorMessage", "Login Failed, please try again");
			showLogin(request, response);
			return;
		}
	}
	
	protected void submitLogout(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getSession().removeAttribute("loggedInUser");
		showIndex(request, response);
	}

	protected void submitPassword(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (checkLoggedIn(request.getSession()) == false) {
			showError(request, response, ErrorCode.AUTHENTICATION, "You must login first.");
			return;
		}
		
		else {
			String oldPassword = request.getParameter("oldPassword");
			String newPassword = request.getParameter("newPassword");
			String newPasswordRepeat = request.getParameter("newPasswordRepeat");
			
			boolean success = false;
			try {
				success = business.updatePassword(oldPassword, newPassword, newPasswordRepeat, getLoggedInProfile(request.getSession()));
			}
			catch (InvalidInputException e) {
				System.out.println(e.getMessage());
				request.setAttribute("errorMessage", e.getMessage());
				showPasswordForm(request, response);
				return;
			} 
			catch (AuthenticationException e) {
				System.out.println(e.getMessage());
				request.setAttribute("errorMessage", e.getMessage());
				showPasswordForm(request, response);
				return;
			}
			catch (ProcessingException e) {
				System.out.println(e.getMessage());
				request.setAttribute("errorMessage", e.getMessage());
				showPasswordForm(request, response);
				return;
			}
						
			if (success)
			{
				showLoggedInProfile(request, response);
			}
			
			else showPasswordForm(request, response);
		}
	}
	
	
    //Admin stuff [POST]
    //AdminSessionEnable(res, req)
    //AdminSessionDisable(res, req)
    
    ////New forms
	protected void showNewPostForm(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		if (checkLoggedIn(request.getSession()) == false) {
			showError(request, response, ErrorCode.AUTHENTICATION, "You must login before posting!");
			return;
		}
		
		int profileId = 0;
		List<BlogDTO> blogs = new ArrayList<BlogDTO>();
		
		try{
			profileId = getLoggedInId(request.getSession());
			blogs = business.getBlogsByProfile(profileId);
		}
		catch(ProcessingException e)
		{
			showError(request, response, ErrorCode.SERVER, e.getMessage());
			return;
		}
			
		request.setAttribute("profileId", profileId);
		request.setAttribute("blogs", blogs);
		getServletContext().getRequestDispatcher("/postNew.jsp").forward(request, response);
	}
	
    protected void showNewBlogForm(HttpServletRequest request, HttpServletResponse response)
    	throws ServletException, IOException {
    	if (checkLoggedIn(request.getSession()) == false)
    	{
    		showError(request, response, ErrorCode.AUTHENTICATION, "You must login before adding a blog!");
    		return;
    	}
	
    	int profileId = 0;
    	
		profileId = getLoggedInId(request.getSession());
		
    	request.setAttribute("profileId", profileId);
		getServletContext().getRequestDispatcher("/blogNew.jsp").forward(request, response);
		return;
}
	
	protected void showNewProfileForm(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		if (checkLoggedIn(request.getSession()) == false) {
			getServletContext().getRequestDispatcher("/profileNew.jsp").forward(request, response);
			return;
		}

		else {
			showLoggedInProfile(request, response);
			return;
			}
	}
	
	//Edit forms [Prepopulate form with DO content]
	protected void showEditPostForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (checkLoggedIn(request.getSession()) == false) {
			showError(request, response, ErrorCode.AUTHENTICATION, "You must login to edit.");
			return;
		}
		
		int postId = 0;
		
		//Which post
		try {
			postId = java.lang.Integer.parseInt(request.getParameter("id"));
		}
		catch (NumberFormatException e) {
			showError(request, response, ErrorCode.SERVER, "Malformed Post ID!");
			return;
		}
		
		//Get the data
		PostDTO post = null;
		try {
			post = business.getPost(postId);
		} 
		catch (ProcessingException e) {
			showError(request, response, ErrorCode.SERVER, "Couldn't retrieve post.");
		}
		
		//Make sure we own the post
		if (post.getProfileId() == getLoggedInId(request.getSession()))
		{
			request.setAttribute("post", post);
			getServletContext().getRequestDispatcher("/postEdit.jsp").forward(request, response);
			return;
		}
		else 
		{
			showError(request, response, ErrorCode.AUTHENTICATION);
			return;
		}
		
	}
	
	protected void showEditBlogForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (checkLoggedIn(request.getSession()) == false) {
			showError(request, response, ErrorCode.AUTHENTICATION, "You must login to edit.");
			return;
		}
		
		int blogId = 0;
		
		//Which post
		try {
			blogId = java.lang.Integer.parseInt(request.getParameter("id"));
		}
		catch (NumberFormatException e) {
			showError(request, response, ErrorCode.SERVER, "Malformed Blog ID!");
			return;
		}
		
		//Get the data
		BlogDTO blog = null;
		try {
			blog = business.getBlog(blogId);
		} 
		catch (ProcessingException e) {
			showError(request, response, ErrorCode.SERVER, "Couldn't retrieve blog.");
		}
		
		//Make sure we own the post
		if (blog.getProfileId() == getLoggedInId(request.getSession()))
		{
			request.setAttribute("blog", blog);
			getServletContext().getRequestDispatcher("/blogEdit.jsp").forward(request, response);
			return;
		}
		else 
		{
			showError(request, response, ErrorCode.AUTHENTICATION);
			return;
		}
	}
	
	protected void showEditProfileForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (checkLoggedIn(request.getSession()) == false) {
			showError(request, response, ErrorCode.NOTFOUND, "You must login to edit.");
			return;
		}
					
		int profileId = getLoggedInId(request.getSession());
		ProfileDTO profile = null;
		try {
			profile = business.getProfile(profileId);
		} catch (ProcessingException e) {
			showError(request, response, ErrorCode.SERVER, "Couldn't retrieve profile.");
			return;
		}
		request.setAttribute("profile", profile);
		getServletContext().getRequestDispatcher("/profileEdit.jsp").forward(request, response);
		return;
	}

	protected void showPasswordForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/passwordEdit.jsp").forward(request, response);
		return;
	}
	
	////Find forms
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
	
    ////Generic pages
	protected void showIndex(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
    	getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
    }
	
	protected void showAbout(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/about.jsp").forward(request, response);
	}
	
	protected void showLogin(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		getServletContext().getRequestDispatcher("/login.jsp").forward(request, response);
		return;
	}
	
	//Data pages
	protected void showProfile(HttpServletRequest request, HttpServletResponse response, int profileId)
			throws ServletException, IOException {
		if (profileId <= 0)
		{
			showError(request, response, ErrorCode.SERVER, "Malformed profile ID.");
			return;
		}
	
		ProfileDTO profile = null;
		List<BlogDTO> blogs = new ArrayList<BlogDTO>();
			
		try {
			profile = business.getProfile(profileId);
		}
		catch (ProcessingException e)
		{
			showError(request, response, ErrorCode.SERVER, e.getMessage());
			return;
		}
		
		try {
			blogs =  business.getBlogsByProfile(profileId);
		}
		catch (ProcessingException e) {
			//Not a failure state but we need to handle the exception
		}
			
		request.setAttribute("profile", profile);
		request.setAttribute("blogs", blogs);
		getServletContext().getRequestDispatcher("/profileView.jsp").forward(request, response);
	}
	
	protected void showBlog(HttpServletRequest request, HttpServletResponse response, int blogId)
			throws ServletException, IOException {
		if (blogId <= 0)
		{
			showError(request, response, ErrorCode.SERVER, "Malformed blog ID.");
			return;
		}
		
		BlogDTO blog = null;
		List<PostDTO> posts = new ArrayList<PostDTO>();
		
		try{
			blog = business.getBlog(blogId);
		}
		
		catch(ProcessingException e)
		{
			showError(request, response, ErrorCode.SERVER, e.getMessage());
			return;
		}
		

		try {
			posts = business.getPostsByBlog(blogId, MAGICPOSTNUMBER);
		}
		catch (ProcessingException e) {
			//Not a failure state but we need to handle the exception
		}

		request.setAttribute("blog", blog);
		request.setAttribute("posts", posts);
		getServletContext().getRequestDispatcher("/blogView.jsp").forward(request, response);
	}	
	
	protected void showPost(HttpServletRequest request, HttpServletResponse response, int postId)
			throws ServletException, IOException {
		if (postId <= 0)
		{
			showError(request, response, ErrorCode.SERVER, "Malformed post ID.");
			return;
		}
		
		PostDTO post = null;
		
		try{
			post = business.getPost(postId);
		}
		catch (ProcessingException e)
		{
			showError(request, response, ErrorCode.SERVER, e.getMessage());
			return;
		}
			
		request.setAttribute("post", post);
		getServletContext().getRequestDispatcher("/postView.jsp").forward(request, response);
	}
	
	//Data pages with special lookups
	protected void showRecentPosts(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		List<PostDTO> posts = new ArrayList<PostDTO>();
		
		try {
			posts = business.getPostsRecent(MAGICPOSTNUMBER);
			request.setAttribute("posts", posts);
		}
		catch (ProcessingException e)
		{
			showError(request, response, ErrorCode.SERVER, e.getMessage());
			return;
		}

		getServletContext().getRequestDispatcher("/postRecent.jsp").forward(request, response);
	}

	//Show Post by ID [PARSES URI]
	protected void showPostById(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int postId = 0;
		
		try {
			postId = java.lang.Integer.parseInt(request.getParameter("id"));
		}
		catch (NumberFormatException e) {
			showError(request, response, ErrorCode.SERVER, "Malformed post ID." + postId);
			return;
		}
		
		showPost(request, response, postId);
	}
	
	//Show Blog by ID [PARSES URI]
    protected void showBlogById(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int blogId = 0;
		
		try {
			blogId = java.lang.Integer.parseInt(request.getParameter("id"));
		}
		catch (NumberFormatException e) {
			showError(request, response, ErrorCode.SERVER, "Malformed blog ID.");
			return;
		}

		showBlog(request, response, blogId);
	}
    
    //Show Profile by ID [PARSES URI]
    protected void showProfileById(HttpServletRequest request, HttpServletResponse response)
    		throws ServletException, IOException {
    	int profileId = 0;
    	try {
    	profileId = java.lang.Integer.parseInt(request.getParameter("id"));
    	}
    	catch (NumberFormatException e) {
    		showError(request, response, ErrorCode.SERVER, "Malformed profile ID.");
    		return;
    	}
    	
    	showProfile(request, response, profileId);
    }
    
    //[PARSES SESSION] //Shows logged in profile or error
    protected void showLoggedInProfile(HttpServletRequest request, HttpServletResponse response)
        	throws ServletException, IOException {
    	if (!checkLoggedIn(request.getSession()))
    	{
    		showError(request, response, ErrorCode.AUTHENTICATION);
    		return;
    	}
    	
		int	profileId = 0;
		
		profileId = getLoggedInId(request.getSession());
		
		showProfile(request, response, profileId);
	}
    
    ////Errors
    protected void showError(HttpServletRequest request, HttpServletResponse response, ErrorCode errorCode, String errorMessage)
			throws ServletException, IOException {
		request.setAttribute("errordetails", errorMessage);
		showError(request, response, errorCode);
    } 
    
    protected void showError(HttpServletRequest request, HttpServletResponse response, ErrorCode errorCode)
		throws ServletException, IOException {
    	switch (errorCode)
    	{
    	case AUTHENTICATION:
    		getServletContext().getRequestDispatcher("/http401.jsp").forward(request, response);
    		break;
    	case FORBIDDEN:
    		getServletContext().getRequestDispatcher("/http403.jsp").forward(request, response);
    		break;
    	case NOTFOUND:
    		getServletContext().getRequestDispatcher("/http404.jsp").forward(request, response);
    		break;
    	case SERVER:
    		getServletContext().getRequestDispatcher("/http500.jsp").forward(request, response);
    		break;
    	default:
    		getServletContext().getRequestDispatcher("/httperr.jsp").forward(request, response);
    		break;
    	}
    }
    
    protected ProfileDTO getLoggedInProfile(HttpSession session)
    {
    	ProfileDTO user = null;
    	
    	user = (ProfileDTO) session.getAttribute("loggedInUser");
    	
    	return user;
    }
    
    //[PARSES SESSION] Gets the ID of the currently logged in user, or 0 if nobody's logged in
    protected int getLoggedInId(HttpSession session)
    {
    	int id = 0;
    	ProfileDTO user = (ProfileDTO) session.getAttribute("loggedInUser");

    	if (user != null)
    	{
    		id = user.getId();
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