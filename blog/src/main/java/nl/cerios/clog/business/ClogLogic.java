package nl.cerios.clog.business;

import nl.cerios.clog.database.v2.PostDAOSQL;
import nl.cerios.clog.database.v2.BlogDAOSQL;
import nl.cerios.clog.database.v2.ProfileDAOSQL;
import nl.cerios.clog.database.v2.PostDTO;
import nl.cerios.clog.database.v2.BlogDTO;
import nl.cerios.clog.database.v2.ProfileDTO;

import nl.cerios.clog.domain.PostDO;
import nl.cerios.clog.domain.BlogDO;
import nl.cerios.clog.domain.ProfileDO;
import nl.cerios.clog.exception.AuthenticationException;
import nl.cerios.clog.exception.DataNotFoundException;
import nl.cerios.clog.exception.InvalidInputException;
import nl.cerios.clog.database.v2.ConnectionFactory;

import java.util.ArrayList;
import java.util.List;

import nl.cerios.clog.database.Authenticator;

import nl.cerios.clog.ui.AppConfiguration;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.ValidationException;

public class ClogLogic {

	ProfileDAOSQL dbProfile;
	BlogDAOSQL dbBlog;
	PostDAOSQL dbPost;
	
	//TODO: Implement images
	//ImageDAOSQL dbPic;
	
	//TODO:TODO:TODO:TODO:											/TODO:/
	//TODO:SANITIZER:TODO:///////////////////////////////////////////////////
	//TODO:TODO:TODO:TODO:											/TODO:/
	
	Authenticator authenticator;
	
	AppConfiguration config;
	int postPagination = 5;

	public void init(AppConfiguration appConfig) {
		postPagination = appConfig.getPostsToRetrieve();
		ConnectionFactory.getInstance().init(appConfig.getUrl(), appConfig.getUsername(), appConfig.getPassword());
	}
	
	//TODO: Something with SQL IllegalStates
	//====Post====/
	public PostDO getPost(int postId) {
		PostDTO post = null; 
		int profileId = 0;
		
		try {
			post = dbPost.getPostById(postId);
			profileId = dbBlog.getProfileIdByBlogId(post.getBlogId()); 
		}
		catch(IllegalStateException e) {
			//TODO: Log error here?
		}
		
		return postDataToDomain(sanitizePost(post), profileId);
	}
	
	public List<PostDO> getPostsRecent(int count) {
		ArrayList<PostDO> posts = new ArrayList<PostDO>();
		List<PostDTO> postData = null;
		
		try {
			postData = dbPost.getPostsRecent(count);
		} catch (IllegalStateException e) {
			//TODO: Log error here?
		}
		
		for(PostDTO p : postData)
		{
			posts.add(postDataToDomain(sanitizePost(p), dbBlog.getProfileIdByBlogId(p.getBlogId())));
		}
		
		return posts;
	}
	
	public List<PostDO> getPostsByBlog(int blogId, int count) {
		ArrayList<PostDO> posts = new ArrayList<PostDO>();
		
		List<PostDTO> postData = dbPost.getPostsByBlogId(blogId, count);
		
		for(PostDTO p : postData)
		{
			posts.add(postDataToDomain(sanitizePost(p), dbBlog.getProfileIdByBlogId(p.getBlogId())));
		}
		
		return posts;
	}
	
	//TODO: Or not TODO? //public List<PostDO> getPostsByBlogSince(int blogId, ZonedDateTime since)
	
	public int insertPost(PostDO newPost, ProfileDO user) 
			throws InvalidInputException, AuthenticationException {
		int newId = 0;
		BlogDTO blog = null;
		
		//Authentication problem
		if (user == null)
		{
			throw new AuthenticationException("Authentication Error: User not logged in");
		}
		
		//Make sure everything exists
		try {
			blog = dbBlog.getBlogById(newPost.getBlogId());
		}
		catch (IllegalStateException e)
		{
			throw new InvalidInputException(e);
		}
		
		//Actual insertion
		if (blog != null)
		{
			if (user.getId() != blog.getProfileId())
			{
				throw new AuthenticationException("Authentication Error: Incorrect User logged in.");
			}
			
			try{
				newId = dbPost.insertPost(sanitizePost(postDomainToData(newPost)));
			}
			
			//Insertion problem
			catch(IllegalStateException e)
			{
				throw new InvalidInputException(e);
			}
		}
		
		return newId;
	}
	
	public boolean updatePost(PostDO updatedPost, ProfileDO user) 
			throws InvalidInputException, AuthenticationException {
		boolean success = false;
		BlogDTO blog = null;
		
		//Authentication problem
		if (user == null)
		{
			throw new AuthenticationException("Authentication Error: User not logged in");
		}
		
		//Make sure everything exists
		try {
			blog = dbBlog.getBlogById(updatedPost.getBlogId());
		}
		catch (IllegalStateException e)
		{
			throw new InvalidInputException(e);
		}
		
		//Actual insertion
		if (blog != null)
		{ 
			if (user.getId() != blog.getProfileId())
			{
				throw new AuthenticationException("Authentication Error: Incorrect User logged in.");
			}
			
			try{
				int id = dbPost.updatePost(sanitizePost(postDomainToData(updatedPost)));
				if (id != 0)
				{
					success = true;
				}
			}
			
			//Insertion problem
			catch(IllegalStateException e)
			{
				throw new InvalidInputException(e);
			}
		}
		
		return success;
	}

	//====Blog====//
	public BlogDO getBlog(int blogId)
	{
		BlogDTO blog = null;
		
		try {
			blog = dbBlog.getBlogById(blogId);
		}
		catch (IllegalStateException e) {
			//TODO: Log Error here?
		}
		
		return blogDataToDomain(sanitizeBlog(blog));
	}
	
	public List<BlogDO> getBlogsByProfile(int profileId) {
		ArrayList<BlogDO> blogs = new ArrayList<BlogDO>();
		List<BlogDTO> blogsdata = null;
		
		try {
			blogsdata = dbBlog.getBlogsByProfileId(profileId);
		}
		catch (IllegalStateException e)
		{
			//TODO: Log error here?
		}
		
		if (blogsdata != null)
		{
			for(BlogDTO b : blogsdata)
			{
				blogs.add(blogDataToDomain(sanitizeBlog(b)));
			}
		}
		
		return blogs;
	}
	
	public int insertBlog(BlogDO newBlog, ProfileDO user) 
			throws InvalidInputException, AuthenticationException {
		int newId = 0;
		ProfileDTO profile = null;
		
		//Authentication problem
		if (user == null)
		{
			throw new AuthenticationException("Authentication Error: User not logged in.");
		}
		
		else if (user.getId() != newBlog.getProfileId())
		{
			throw new AuthenticationException("Authentication Error: Incorrect User logged in.");
		}
		
		//Make sure everything exists
		try {
			profile = dbProfile.getProfileById(newBlog.getProfileId());
		}
		catch (IllegalStateException e)
		{
			throw new InvalidInputException(e);
		}
		
		//Actual insertion
		if (profile != null)
		{
			if (profile.getId() != newBlog.getProfileId())
			{
				throw new AuthenticationException("Authentication Error: Incorrect User logged in.");
			}
			
			try{
				newId = dbBlog.insertBlog(sanitizeBlog(blogDomainToData(newBlog)));
			}
			
			//Insertion problem
			catch(IllegalStateException e)
			{
				throw new InvalidInputException(e);
			}
		}
		
		return newId;
	}
	
	public boolean updateBlog(BlogDO updatedBlog, ProfileDO user)
			throws InvalidInputException, AuthenticationException {
		boolean success = false;
		ProfileDTO profile = null;
		
		//Authentication problem
		if (user == null)
		{
			throw new AuthenticationException("Authentication Error: User not logged in");
		}
		
		else if (user.getId() == updatedBlog.getProfileId())
		{
			throw new AuthenticationException("Authentication Error: Incorrect User logged in.");
		}
		
		//Make sure everything exists
		try {
			profile = dbProfile.getProfileById(updatedBlog.getProfileId());
		}
		catch (IllegalStateException e)
		{
			throw new InvalidInputException(e);
		}
		
		//Actual insertion
		if (updatedBlog != null)
		{
			if (profile.getId() != updatedBlog.getProfileId())
			{
				throw new AuthenticationException("Authentication Error: Incorrect User logged in.");
			}
			
			try{
				int newId = 0;
				newId = dbBlog.insertBlog(sanitizeBlog(blogDomainToData(updatedBlog)));
				if (newId != 0)
				{
					success = true;
				}
			}
			
			//Insertion problem
			catch(IllegalStateException e)
			{
				throw new InvalidInputException(e);
			}
		}
		
		return success;
	}

	//====Profile====//
	public ProfileDO getProfile(int profileId) {
		ProfileDTO profile = null; 
		
		
		try {
			profile = dbProfile.getProfileById(profileId); 
		}
		catch(IllegalStateException e) {
			//TODO: Log error here?
		}
		
		return profileDataToDomain(sanitizeProfile(profile));
	}

	//TODO: Insert new profile (Rewrite Authenticator!)
	//public int insertProfile(ProfileDO newProfile, String password) throws InvalidInputException
	//public boolean updateProfile(ProfileDO editedProfile, ProfileDO user) throws InvalidInputException, AuthenticationException
	
	//====Cleaning & Converting====//
	//Clean Post
	public PostDTO sanitizePost(PostDTO post) {
		PostDTO cleanPost;

		String title = post.getTitle();
		String text = post.getText();

		String cleanTitle = "Title";
		String cleanText = "Text";

		try {
			cleanTitle = ESAPI.validator().getValidSafeHTML("postTitle", title, 48, true);
			cleanText = ESAPI.validator().getValidSafeHTML("postText", text, 64000, true);
		}
		catch (ValidationException e)  { System.err.println("Error validating Post."); }

		cleanPost = new PostDTO(post.getId(), post.getBlogId(), post.getTimestamp(), cleanTitle, cleanText);

		return cleanPost;
	}
	
	public List<PostDTO> sanitizePost(List<PostDTO> postList)
			throws InvalidInputException {
		List<PostDTO> cleanList = new ArrayList<PostDTO>();

		for (PostDTO p : postList) {
			PostDTO cleanPost = sanitizePost(p);
			cleanList.add(cleanPost);
		}

		return cleanList;
	}
	
	//Convert Post
	public PostDTO postDomainToData(PostDO post) {
		PostDTO convertedPost = null;
		
		if (post != null)
		{
			convertedPost = new PostDTO(post.getId(), post.getBlogId(), post.getTimestamp(), post.getTitle(), post.getText());
		}
		
		return convertedPost;
	}
	
	public PostDO postDataToDomain(PostDTO post, int profileId) {
		PostDO convertedPost = null;
		
		if (post != null)
		{
			convertedPost = new PostDO(post.getId(), post.getBlogId(), profileId, post.getTimestamp(), post.getTitle(), post.getText());
		}
		
		return convertedPost;
	}
	
	//Clean Blog	
	public BlogDTO sanitizeBlog(BlogDTO blog) {
    	BlogDTO cleanBlog;
    	
    	String title = blog.getTitle();
    	String description = blog.getDescription();
    	
    	String cleanTitle = "Title";
    	String cleanDescription = "Description";

    	try
    	{
    		cleanTitle = ESAPI.validator().getValidSafeHTML("blogTitle", title, 48, true);
    		cleanDescription = ESAPI.validator().getValidSafeHTML("blogDescription", description, 32000, true);
    	} 	
    	catch (ValidationException e) { System.err.println("Error validating Blog"); }
    	
    	cleanBlog = new BlogDTO(blog.getId(), blog.getProfileId(), cleanTitle, cleanDescription);
    	
    	return cleanBlog;
	}

	public List<BlogDTO> sanitizeBlog(List<BlogDTO> blogList)
			throws InvalidInputException {
		List<BlogDTO> cleanList = new ArrayList<BlogDTO>();

		for (BlogDTO p : blogList) {
			BlogDTO cleanBlog = sanitizeBlog(p);
			cleanList.add(cleanBlog);
		}

		return cleanList;
	}
	
	//Convert Blog 
	public BlogDTO blogDomainToData(BlogDO blog) {
		BlogDTO convertedBlog = null;
		
		if (blog != null)
		{
			convertedBlog = new BlogDTO(blog.getId(), blog.getProfileId(), blog.getTitle(), blog.getDescription());
		}
		
		return convertedBlog;
	}
	
	public BlogDO blogDataToDomain(BlogDTO blog) {
		BlogDO convertedBlog = null;
		
		if (blog != null)
		{
			convertedBlog = new BlogDO(blog.getId(), blog.getProfileId(), blog.getTitle(), blog.getDescription());
		}
		
		return convertedBlog;
	}

	// Clean Profile
	public ProfileDTO sanitizeProfile(ProfileDTO profile) {
    	ProfileDTO cleanProfile;
    	
    	String name = profile.getName();
    	String motto = profile.getMotto();
    	
    	String cleanName = "Name";
    	String cleanMotto = "Motto";
    	
    	try
    	{
    		cleanName = ESAPI.validator().getValidSafeHTML("profileName", name, 16, true);
    		cleanMotto = ESAPI.validator().getValidSafeHTML("profileMotto", motto, 32, true);
    	} 	
    	catch (ValidationException e) { System.err.println("Error validating Profile"); }
    	
    	cleanProfile = new ProfileDTO(profile.getId(), cleanName, cleanMotto, profile.getJoinDate());
    	
    	return cleanProfile;
	}

	public List<ProfileDTO> sanitizeProfile(List<ProfileDTO> profileList)
			throws InvalidInputException {
		List<ProfileDTO> cleanList = new ArrayList<ProfileDTO>();

		for (ProfileDTO p : profileList) {
			ProfileDTO cleanProfile = sanitizeProfile(p);
			cleanList.add(cleanProfile);
		}

		return cleanList;
	}
	
	// Convert Profile
	public ProfileDTO profileDomainToData(ProfileDO profile) {
		ProfileDTO convertedProfile = null;
		
		if (profile != null)
		{
			convertedProfile = new ProfileDTO(profile.getId(), profile.getName(), profile.getMotto(), profile.getJoinDate());
		}
		
		return convertedProfile;
	}
	
	public ProfileDO profileDataToDomain(ProfileDTO profile) {
		ProfileDO convertedProfile = null;
		
		if (profile != null)
		{
			convertedProfile = new ProfileDO(profile.getId(), profile.getName(), profile.getMotto(), profile.getJoinDate());
		}
		
		return convertedProfile;
	}
	
	//====Auth====//
	//public int checkPassword(String username, String password) throws InvalidInputException, AuthenticationException
	//public bool insertPassword(ProfileDO profile, String password) throws InvalidInputException, AuthenticationException
}