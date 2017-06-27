package nl.cerios.clog.business;

import java.util.List;
import java.util.ArrayList;

import nl.cerios.clog.database.v2.ConnectionFactory;
import nl.cerios.clog.database.v2.PostDAOSQL;
import nl.cerios.clog.database.v2.BlogDAOSQL;
import nl.cerios.clog.database.v2.ProfileDAOSQL;
import nl.cerios.clog.database.Authenticator;

import nl.cerios.clog.database.v2.PostDTO;
import nl.cerios.clog.database.v2.BlogDTO;
import nl.cerios.clog.database.v2.ProfileDTO;

import nl.cerios.clog.domain.PostDO;
import nl.cerios.clog.domain.BlogDO;
import nl.cerios.clog.domain.ProfileDO;

import org.owasp.esapi.ESAPI;

import nl.cerios.clog.exception.AuthenticationException;
import nl.cerios.clog.exception.InvalidInputException;
import nl.cerios.clog.exception.ProcessingException;

import java.sql.SQLException;
import org.owasp.esapi.errors.ValidationException;

public class ClogLogic {
	ProfileDAOSQL dbProfile = new ProfileDAOSQL();
	BlogDAOSQL dbBlog = new BlogDAOSQL();
	PostDAOSQL dbPost = new PostDAOSQL();
	
	//TODO: Implement images
	//ImageDAOSQL dbPic;
	
	//TODO: Rewrite authentication for salt and hash in db
	Authenticator authenticator;
	
	//GET: DATA -> DOMAIN -> CLEAN
	//INSERT: DOMAIN -> CLEAN -> DATA
	
	AppConfiguration config;
	
	//TODO: Set this here? Or allow the user to request a range with this as max?
	int postPagination = 5;

	public void init(AppConfiguration appConfig) {
		postPagination = appConfig.getPostsToRetrieve();
		ConnectionFactory.getInstance().init(appConfig.getUrl(), appConfig.getUsername(), appConfig.getPassword());
	}
	
	//====Post====/
	public PostDO getPost(int postId) 
		throws ProcessingException {
		PostDTO postData = null; 
		PostDO postDomain = null;
		PostDO cleanPost = null;
		
		try {
			postData = dbPost.getPostById(postId);		
			}
		catch (SQLException e)
		{
			throw new ProcessingException("Error retrieving post.", e);
		}
		
		if (postData == null)
		{
			throw new ProcessingException("Post doesn't exist.");
		}
		
		try {
			postDomain = postDataToDomain(postData, dbBlog.getProfileIdByBlogId(postData.getBlogId()));			
			cleanPost = sanitizePost(postDomain);
		}
		catch (SQLException | ValidationException e) {
			throw new ProcessingException("Error processing post.", e);
		}
		
		return cleanPost;
	}
	
	public List<PostDO> getPostsRecent(int count) 
		throws ProcessingException {
		List<PostDTO> postsData = new ArrayList<PostDTO>();
		List<PostDO> postsDomain = new ArrayList<PostDO>();
		List<PostDO> cleanPosts = new ArrayList<PostDO>();
		
		try {
			postsData = dbPost.getPostsRecent(count);		
		}
		catch (SQLException e)
		{
			throw new ProcessingException("Error retrieving posts.", e);
		}
		
		if (postsData.isEmpty())
		{
			throw new ProcessingException("Couldn't retrieve posts.");
		}
		
		try {
			for (PostDTO p : postsData)
			{
				postsDomain.add(postDataToDomain(p, dbBlog.getProfileIdByBlogId(p.getBlogId())));
			}
			cleanPosts = sanitizePosts(postsDomain);	
		}
		catch (SQLException | ValidationException e) {
			throw new ProcessingException("Error processing posts.", e);
		}
		
		return cleanPosts;
	}
	
	public List<PostDO> getPostsByBlog(int blogId, int count) 
		throws ProcessingException {
		List<PostDTO> postsData = new ArrayList<PostDTO>();
		List<PostDO> postsDomain = new ArrayList<PostDO>();
		List<PostDO> cleanPosts = new ArrayList<PostDO>();
		
		try{
			postsData = dbPost.getPostsByBlogId(blogId, count);
		}
		catch (SQLException e)
		{
			throw new ProcessingException("Error retrieving profile.", e);
		}
		
		if (postsData.isEmpty())
		{
			throw new ProcessingException("Couldn't retrieve posts.");
		}
		
		try {
			for(PostDTO p : postsData)
			{
				postsDomain.add(postDataToDomain(p, dbBlog.getProfileIdByBlogId(p.getBlogId())));
			}
			cleanPosts = sanitizePosts(postsDomain);
		}
		catch (SQLException | ValidationException e)
		{
			throw new ProcessingException("Error processing posts.",  e);
		}
		
		return cleanPosts;
	}
	
	//TODO: Or not TODO? //public List<PostDO> getPostsByBlogSince(int blogId, ZonedDateTime since)
	
	public int insertPost(PostDO newPost, ProfileDO user) 
			throws AuthenticationException, InvalidInputException, ProcessingException {
		int newId = 0;
		PostDO cleanPost = null;
		PostDTO postData = null;
		BlogDTO blog = null;
		
		//Authentication problem
		if (user == null)
		{
			throw new AuthenticationException("User not logged in");
		}
		
		//Make sure everything exists
		try {
			blog = dbBlog.getBlogById(newPost.getBlogId());
		}
		catch (SQLException e)
		{
			throw new InvalidInputException("Blog doesn't exist.", e);
		}
		
		//Actual insertion
		if (blog != null)
		{
			if (user.getId() != blog.getProfileId())
			{
				throw new AuthenticationException("Incorrect user logged in.");
			}
			
			try{
				cleanPost = sanitizePost(newPost);
				postData = postDomainToData(cleanPost);
			}
			catch(ValidationException e)
			{
				throw new ProcessingException("Error processing post.", e);
			}
			
			try {
				newId = dbPost.insertPost(postData);
			}
			catch(SQLException e)
			{
				throw new ProcessingException("Error saving post.", e);
			}
		}
		
		return newId;
	}
	
	public boolean updatePost(PostDO updatedPost, ProfileDO user) 
			throws AuthenticationException, InvalidInputException, ProcessingException {
		boolean success = false;		
		PostDO cleanPost = null;
		PostDTO postData = null;
		BlogDTO blog = null;
		
		//Authentication problem
		if (user == null)
		{
			throw new AuthenticationException("User not logged in.");
		}
		
		//Make sure everything exists
		try {
			blog = dbBlog.getBlogById(updatedPost.getBlogId());
		}
		catch (SQLException e)
		{
			throw new InvalidInputException("Blog doesn't exist", e);
		}
		
		//Actual insertion
		if (blog != null)
		{ 
			if (user.getId() != blog.getProfileId())
			{
				throw new AuthenticationException("Incorrect user logged in.");
			}
			
			try{
				cleanPost = sanitizePost(updatedPost);
				postData = postDomainToData(cleanPost);
			}
			catch(ValidationException e)
			{
				throw new ProcessingException("Error processing post.", e);
			}
			
			try {
				int newId = dbPost.updatePost(postData);
				success = (newId != 0);			
			}
			catch(SQLException e)
			{
				throw new ProcessingException("Error saving post.", e);
			}
		}
		
		return success;
	}
	
	//====Blog====//
	public BlogDO getBlog(int blogId)
		throws ProcessingException {
		BlogDTO blogData = null;
		BlogDO blogDomain = null;
		BlogDO cleanBlog = null;
		
		try {
			blogData = dbBlog.getBlogById(blogId);
		}
		catch (SQLException e)
		{
			throw new ProcessingException("Error retrieving blog.", e);
		}

		if (blogData == null)
		{
			throw new ProcessingException("Blog doesn't exist.");
		}
		
		try {
			blogDomain = blogDataToDomain(blogData);
			cleanBlog = sanitizeBlog(blogDomain);
		}
		catch (ValidationException e) {
			throw new ProcessingException("Error processing blog.", e);
		}
		
		return cleanBlog;
	}

	public List<BlogDO> getBlogsByProfile(int profileId)
			throws ProcessingException {
		List<BlogDTO> blogsData = new ArrayList<BlogDTO>();
		List<BlogDO> blogsDomain = new ArrayList<BlogDO>();
		List<BlogDO> cleanBlogs = new ArrayList<BlogDO>();
		
		try {
			blogsData = dbBlog.getBlogsByProfileId(profileId);
		}
		catch (SQLException e)
		{
			throw new ProcessingException("Error retreiving blogs", e);
		}
		
		if (blogsData.isEmpty())
		{
			throw new ProcessingException("Couldn't retrieve blogs.");
		}
		
		try {
			for(BlogDTO b : blogsData)
			{				
				blogsDomain.add(blogDataToDomain(b));
			}
			cleanBlogs = sanitizeBlogs(blogsDomain);
		}
		catch (ValidationException e)
		{
			throw new ProcessingException("Error processing blogs.", e);
		}
		
		return cleanBlogs;
	}
	
	public int insertBlog(BlogDO newBlog, ProfileDO user) 
			throws InvalidInputException, AuthenticationException, ProcessingException {
		int newId = 0;
		BlogDO cleanBlog = null;
		BlogDTO blogData = null;
		ProfileDTO profile = null;
		
		//Authentication problem
		if (user == null)
		{
			throw new AuthenticationException("User not logged in.");
		}
		else if (user.getId() != newBlog.getProfileId())
		{
			throw new AuthenticationException("Incorrect user logged in.");
		}
		
		//Make sure everything exists
		try {
			profile = dbProfile.getProfileById(newBlog.getProfileId());
		}
		catch (SQLException e)
		{
			throw new InvalidInputException("Profile doesn't exist", e);
		}
		
		//Actual insertion
		if (profile != null)
		{
			if (profile.getId() != newBlog.getProfileId())
			{
				throw new AuthenticationException("Incorrect User logged in.");
			}
			
			try {
				cleanBlog = sanitizeBlog(newBlog);
				blogData = blogDomainToData(cleanBlog);
			}
			catch(ValidationException e)
			{
				throw new ProcessingException("Error processing blog", e);
			}
			
			try {
				newId = dbBlog.insertBlog(blogData);
			}
			catch (SQLException e)
			{
				throw new ProcessingException("Error saving profile.", e);
			}
		}
		
		return newId;
	}

	public boolean updateBlog(BlogDO updatedBlog, ProfileDO user)
			throws InvalidInputException, AuthenticationException {
		boolean success = false;
		BlogDO cleanBlog = null;
		BlogDTO blogData = null;
		ProfileDTO profile = null;
		
		//Authentication problem
		if (user == null)
		{
			throw new AuthenticationException("User not logged in");
		}
		else if (user.getId() == updatedBlog.getProfileId())
		{
			throw new AuthenticationException("Incorrect User logged in.");
		}
		
		//Make sure everything exists
		try {
			profile = dbProfile.getProfileById(updatedBlog.getProfileId());
		}
		catch (SQLException e)
		{
			throw new InvalidInputException(e);
		}
		
		//Actual insertion
		if (updatedBlog != null)
		{
			if (profile.getId() != updatedBlog.getProfileId())
			{
				throw new AuthenticationException("Incorrect User logged in.");
			}
			
			try {
				cleanBlog = sanitizeBlog(updatedBlog);
				blogData = blogDomainToData(cleanBlog);
				int newId = dbBlog.insertBlog(blogData);
				success = (newId != 0);
			}
			//Filtering | Insertion problem
			catch(ValidationException| SQLException e) {
				throw new InvalidInputException(e);
			}
		}
		
		return success;
	}

	//====Profile====//
	public ProfileDO getProfile(int profileId)
			throws ProcessingException {
		ProfileDTO profileData = null;
		ProfileDO profileDomain = null;
		ProfileDO cleanProfile = null;
		
		try {
			profileData = dbProfile.getProfileById(profileId);
		}
		catch (SQLException e)
		{
			throw new ProcessingException("Error retrieving profile.", e);
		}
		
		if (profileData == null)
		{
			throw new ProcessingException("Profile doesn't exist.");
		}
		
		try {
			profileDomain = profileDataToDomain(profileData);
			cleanProfile = sanitizeProfile(profileDomain);	
		}
		catch(ValidationException e) {
			throw new ProcessingException("Error processing profile.", e);
		}
		
		return cleanProfile;
	}
	
	public int insertProfile(ProfileDO newProfile, String password) 
			throws InvalidInputException, ProcessingException {
		int newId = 0;
		
		ProfileDO cleanProfile = null;
		ProfileDTO profileData = null;
		
		try{
			cleanProfile = sanitizeProfile(newProfile);
			profileData = profileDomainToData(cleanProfile);
		}	
		
		catch(ValidationException e)
		{
			throw new ProcessingException("Error processing Profile.", e);
		}
		
		
		try {
			newId = dbProfile.insertProfile(profileData);
			authenticator.InsertPassword(profileData.getName(), password);
		}
		
		catch(SQLException e)
		{
			throw new ProcessingException("Error saving Profile.", e);
		}
		
		return newId;
	}
	
	public boolean updateProfile(ProfileDO updatedProfile, ProfileDO user)
			throws InvalidInputException, AuthenticationException{
		boolean success = false;
		ProfileDO cleanProfile = null;
		ProfileDTO profileData = null;
		
		//Authentication problem
		if (user == null)
		{
			throw new AuthenticationException("User not logged in");
		}
		else if (user.getId() == updatedProfile.getId())
		{
			throw new AuthenticationException("Incorrect User logged in.");
		}
		
		//Actual insertion
		if (updatedProfile != null)
		{
			try {
				cleanProfile = sanitizeProfile(updatedProfile);
				profileData = profileDomainToData(cleanProfile);
				int newId = dbProfile.insertProfile(profileData);
				success = (newId != 0);
			}
			//Filtering | Insertion problem
			catch(ValidationException| SQLException e) 
			{
				throw new InvalidInputException(e);
			}
		}
				
		return success;
	}

	//====Cleaning & Converting====//
	//Clean Post
	public PostDO sanitizePost(PostDO post)
		throws ValidationException {
		PostDO cleanPost = null;

		if (post != null)
		{
			String title = post.getTitle();
			String text = post.getText();

			String cleanTitle = "Title";
			String cleanText = "Text";

			cleanTitle = ESAPI.validator().getValidSafeHTML("postTitle", title, 48, true);
			cleanText = ESAPI.validator().getValidSafeHTML("postText", text, 64000, true);

			cleanPost = new PostDO(post.getId(), post.getBlogId(), post.getProfileId(), post.getTimestamp(), cleanTitle, cleanText);
		}
		
		return cleanPost;
	}
	
	public List<PostDO> sanitizePosts(List<PostDO> postList)
			throws ValidationException {
		List<PostDO> cleanList = new ArrayList<PostDO>();

		for (PostDO p : postList) {
			PostDO cleanPost = sanitizePost(p);
			cleanList.add(cleanPost);
		}

		return cleanList;
	}
	
	//Clean Blog	
	public BlogDO sanitizeBlog(BlogDO blog) 
		throws ValidationException {
    	BlogDO cleanBlog = null;
    	
    	if (blog != null)
    	{
    		String title = blog.getTitle();
    		String description = blog.getDescription();
    	
    		String cleanTitle = "Title";
    		String cleanDescription = "Description";

    		cleanTitle = ESAPI.validator().getValidSafeHTML("blogTitle", title, 48, true);
    		cleanDescription = ESAPI.validator().getValidSafeHTML("blogDescription", description, 32000, true);
    	
    		cleanBlog = new BlogDO(blog.getId(), blog.getProfileId(), cleanTitle, cleanDescription);
    	}
    	
    	return cleanBlog;
	}

	public List<BlogDO> sanitizeBlogs(List<BlogDO> blogList)
			throws ValidationException {
		List<BlogDO> cleanList = new ArrayList<BlogDO>();

		for (BlogDO p : blogList) {
			BlogDO cleanBlog = sanitizeBlog(p);
			cleanList.add(cleanBlog);
		}

		return cleanList;
	}
	
	// Clean Profile
	public ProfileDO sanitizeProfile(ProfileDO profile) 
		throws ValidationException {
    	ProfileDO cleanProfile = null;
    	
    	if (profile != null)
    	{
	    	String name = profile.getName();
	    	String motto = profile.getMotto();
	    	
	    	String cleanName = "Name";
	    	String cleanMotto = "Motto";
	    	
	    	cleanName = ESAPI.validator().getValidSafeHTML("profileName", name, 16, true);
	    	cleanMotto = ESAPI.validator().getValidSafeHTML("profileMotto", motto, 32, true);
	    	
	    	cleanProfile = new ProfileDO(profile.getId(), cleanName, cleanMotto, profile.getJoinDate());
    	}
    	
    	return cleanProfile;
	}

	public List<ProfileDO> sanitizeProfiles(List<ProfileDO> profileList)
			throws ValidationException {
		List<ProfileDO> cleanList = new ArrayList<ProfileDO>();

		for (ProfileDO p : profileList) {
			ProfileDO cleanProfile = sanitizeProfile(p);
			cleanList.add(cleanProfile);
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