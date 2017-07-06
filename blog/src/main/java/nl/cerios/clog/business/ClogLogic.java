package nl.cerios.clog.business;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import nl.cerios.clog.database.ConnectionFactory;
import nl.cerios.clog.database.AuthDAOSQL;
import nl.cerios.clog.database.AuthDO;
import nl.cerios.clog.database.BlogDAOSQL;
import nl.cerios.clog.database.BlogDO;
import nl.cerios.clog.database.PostDAOSQL;
import nl.cerios.clog.database.PostDO;
import nl.cerios.clog.database.ProfileDAOSQL;
import nl.cerios.clog.database.ProfileDO;

import org.owasp.esapi.ESAPI;

import nl.cerios.clog.exception.AuthenticationException;
import nl.cerios.clog.exception.InvalidInputException;
import nl.cerios.clog.exception.ProcessingException;
import nl.cerios.clog.object.BlogDTO;
import nl.cerios.clog.object.PostDTO;
import nl.cerios.clog.object.ProfileDTO;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.sql.SQLException;

import org.owasp.esapi.errors.EncryptionException;
import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.reference.crypto.JavaEncryptor;

public class ClogLogic {
	private SecureRandom random = new SecureRandom();

	private ProfileDAOSQL dbProfile = new ProfileDAOSQL();
	private BlogDAOSQL dbBlog = new BlogDAOSQL();
	private PostDAOSQL dbPost = new PostDAOSQL();
	private AuthDAOSQL dbHash = new AuthDAOSQL();
	
	//TODO: Implement images
	//ImageDAOSQL dbPic;
	
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
	public PostDTO getPost(int postId) 
		throws ProcessingException {
		PostDO postData = null; 
		PostDTO postDomain = null;
		PostDTO cleanPost = null;
		
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
			postDomain = postDataToObject(postData, dbBlog.getProfileIdByBlogId(postData.getBlogId()));			
			cleanPost = sanitizePost(postDomain);
		}
		catch (SQLException | ValidationException e) {
			throw new ProcessingException("Error processing post.", e);
		}
		
		return cleanPost;
	}
	
	public List<PostDTO> getPostsRecent(int count) 
		throws ProcessingException {
		List<PostDO> postsData = new ArrayList<PostDO>();
		List<PostDTO> postsDomain = new ArrayList<PostDTO>();
		List<PostDTO> cleanPosts = new ArrayList<PostDTO>();
		
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
			for (PostDO p : postsData)
			{
				postsDomain.add(postDataToObject(p, dbBlog.getProfileIdByBlogId(p.getBlogId())));
			}
			cleanPosts = sanitizePosts(postsDomain);	
		}
		catch (SQLException | ValidationException e) {
			throw new ProcessingException("Error processing posts.", e);
		}
		
		return cleanPosts;
	}
	
	public List<PostDTO> getPostsByBlog(int blogId, int count) 
		throws ProcessingException {
		List<PostDO> postsData = new ArrayList<PostDO>();
		List<PostDTO> postsDomain = new ArrayList<PostDTO>();
		List<PostDTO> cleanPosts = new ArrayList<PostDTO>();
		
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
			for (PostDO p : postsData)
			{
				postsDomain.add(postDataToObject(p, dbBlog.getProfileIdByBlogId(p.getBlogId())));
			}
			cleanPosts = sanitizePosts(postsDomain);
		}
		catch (SQLException | ValidationException e) {
			throw new ProcessingException("Error processing posts.",  e);
		}
		
		return cleanPosts;
	}
	
	//TODO: Or not TODO? //public List<PostDO> getPostsByBlogSince(int blogId, ZonedDateTime since)
	
	public int insertPost(PostDTO newPost, ProfileDTO user) 
			throws AuthenticationException, InvalidInputException, ProcessingException {
		int newId = 0;
		PostDTO cleanPost = null;
		PostDO postData = null;
		BlogDO blog = null;
		
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
				postData = postObjectToData(cleanPost);
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
	
	public boolean updatePost(PostDTO updatedPost, ProfileDTO user) 
			throws AuthenticationException, InvalidInputException, ProcessingException {
		boolean success = false;		
		PostDTO cleanPost = null;
		PostDO postData = null;
		BlogDO blog = null;
		
		//Authentication problem
		if (user == null)
		{
			throw new AuthenticationException("User not logged in.");
		}
		else if (user.getId() != updatedPost.getProfileId())
		{
			throw new AuthenticationException("Incorrect User logged in.");
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
				postData = postObjectToData(cleanPost);
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
	public BlogDTO getBlog(int blogId)
		throws ProcessingException {
		BlogDO blogData = null;
		BlogDTO blogDomain = null;
		BlogDTO cleanBlog = null;
		
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

	public List<BlogDTO> getBlogsByProfile(int profileId)
			throws ProcessingException {
		List<BlogDO> blogsData = new ArrayList<BlogDO>();
		List<BlogDTO> blogsDomain = new ArrayList<BlogDTO>();
		List<BlogDTO> cleanBlogs = new ArrayList<BlogDTO>();
		
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
			for(BlogDO b : blogsData)
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
	
	public int insertBlog(BlogDTO newBlog, ProfileDTO user) 
			throws InvalidInputException, AuthenticationException, ProcessingException {
		int newId = 0;
		BlogDTO cleanBlog = null;
		BlogDO blogData = null;
		ProfileDO profile = null;
		
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
				blogData = blogObjectToData(cleanBlog);
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

	public boolean updateBlog(BlogDTO updatedBlog, ProfileDTO user)
			throws InvalidInputException, AuthenticationException {
		boolean success = false;
		BlogDTO cleanBlog = null;
		BlogDO blogData = null;
		ProfileDO profile = null;
		
		//Authentication problem
		if (user == null)
		{
			throw new AuthenticationException("User not logged in");
		}
		else if (user.getId() != updatedBlog.getProfileId())
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
				blogData = blogObjectToData(cleanBlog);
				int newId = dbBlog.updateBlog(blogData);
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
	public ProfileDTO getProfile(int profileId)
			throws ProcessingException {
		ProfileDO profileData = null;
		ProfileDTO profileDomain = null;
		ProfileDTO cleanProfile = null;
		
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
			profileDomain = profileDataToObject(profileData);
			cleanProfile = sanitizeProfile(profileDomain);	
		}
		catch(ValidationException e) {
			throw new ProcessingException("Error processing profile.", e);
		}
		
		return cleanProfile;
	}
	
	public ProfileDTO getProfile(String username)
			throws ProcessingException {
		ProfileDO profileData = null;
		ProfileDTO profileDomain = null;
		ProfileDTO cleanProfile = null;
		
		try {
			profileData = dbProfile.getProfileByName(username);
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
			profileDomain = profileDataToObject(profileData);
			cleanProfile = sanitizeProfile(profileDomain);	
		}
		catch(ValidationException e) {
			throw new ProcessingException("Error processing profile.", e);
		}
		
		return cleanProfile;
	}
	
	public int insertProfile(ProfileDTO newProfile, String password) 
			throws InvalidInputException, ProcessingException {
		if (password.equals(""))
		{
			throw new InvalidInputException("Password cannot be blank");
		}
		
		int newId = 0;
		ProfileDTO cleanProfile = null;
		ProfileDO profileData = null;
		
		try{
			cleanProfile = sanitizeProfile(newProfile);
			profileData = profileObjectToData(cleanProfile);
		}		
		catch(ValidationException e)
		{
			throw new ProcessingException("Error processing Profile.", e);
		}
		
		
		try {
			newId = dbProfile.insertProfile(profileData);
			AuthDO newUser = new AuthDO(newId, hashPassword(charsToBytes(password.toCharArray())));
			dbHash.insertAuth(newUser);			
		}		
		catch(SQLException e)
		{
			throw new ProcessingException("Error saving Profile.", e);
		}
		
		return newId;
	}
	
	public boolean updateProfile(ProfileDTO updatedProfile, ProfileDTO user)
			throws InvalidInputException, AuthenticationException{
		boolean success = false;
		ProfileDTO cleanProfile = null;
		ProfileDO profileData = null;
		
		//Authentication problem
		if (user == null)
		{
			throw new AuthenticationException("User not logged in");
		}
		else if (user.getId() != updatedProfile.getId())
		{
			throw new AuthenticationException("Incorrect User logged in.");
		}
		
		//Actual insertion
		if (updatedProfile != null)
		{
			try {
				cleanProfile = sanitizeProfile(updatedProfile);
				profileData = profileObjectToData(cleanProfile);
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

	public boolean updatePassword(String oldPassword, String newPassword, String newPasswordRepeat, ProfileDTO user)
			throws InvalidInputException, AuthenticationException, ProcessingException {
		boolean success = false;
		
		if (user == null)
		{
			throw new AuthenticationException("User not logged in");
		}
		
		if (authenticateUser(user.getName(), oldPassword) == false)
		{
			throw new AuthenticationException("Invalid Credentials.");
		}
		if (newPassword.equals(newPasswordRepeat) == false)
		{
			throw new InvalidInputException("New passwords don't match");
		}
		if (newPassword.equals("") || newPasswordRepeat.equals(""))
		{
			throw new InvalidInputException("Password cannot be blank");
		}
		
		try {
			String hash = hashPassword(charsToBytes(newPassword.toCharArray()));
			success = dbHash.updateAuth(new AuthDO(user.getId(), hash));
		} catch (SQLException e) {
			throw new ProcessingException("Error updating profile.");
		}
		
		return success;
	}
	
	//====Cleaning & Converting====//
	//Clean Post
	private PostDTO sanitizePost(PostDTO post)
		throws ValidationException {
		PostDTO cleanPost = null;

		if (post != null)
		{
			String title = post.getTitle();
			String text = post.getText();

			String cleanTitle = "Title";
			String cleanText = "Text";

			cleanTitle = ESAPI.validator().getValidSafeHTML("postTitle", title, 48, true);
			cleanText = ESAPI.validator().getValidSafeHTML("postText", text, 64000, true);

			cleanPost = new PostDTO(post.getId(), post.getBlogId(), post.getProfileId(), post.getTimestamp(), cleanTitle, cleanText);
		}
		
		return cleanPost;
	}
	
	private List<PostDTO> sanitizePosts(List<PostDTO> postList)
			throws ValidationException {
		List<PostDTO> cleanList = new ArrayList<PostDTO>();

		for (PostDTO p : postList) {
			PostDTO cleanPost = sanitizePost(p);
			cleanList.add(cleanPost);
		}

		return cleanList;
	}
	
	//Clean Blog	
	private BlogDTO sanitizeBlog(BlogDTO blog) 
		throws ValidationException {
    	BlogDTO cleanBlog = null;
    	
    	if (blog != null)
    	{
    		String title = blog.getTitle();
    		String description = blog.getDescription();
    	
    		String cleanTitle = "Title";
    		String cleanDescription = "Description";

    		cleanTitle = ESAPI.validator().getValidSafeHTML("blogTitle", title, 48, true);
    		cleanDescription = ESAPI.validator().getValidSafeHTML("blogDescription", description, 32000, true);
    	
    		cleanBlog = new BlogDTO(blog.getId(), blog.getProfileId(), cleanTitle, cleanDescription);
    	}
    	
    	return cleanBlog;
	}

	private List<BlogDTO> sanitizeBlogs(List<BlogDTO> blogList)
			throws ValidationException {
		List<BlogDTO> cleanList = new ArrayList<BlogDTO>();

		for (BlogDTO p : blogList) {
			BlogDTO cleanBlog = sanitizeBlog(p);
			cleanList.add(cleanBlog);
		}

		return cleanList;
	}
	
	// Clean Profile
	private ProfileDTO sanitizeProfile(ProfileDTO profile) 
		throws ValidationException {
    	ProfileDTO cleanProfile = null;
    	
    	if (profile != null)
    	{
	    	String name = profile.getName();
	    	String motto = profile.getMotto();
	    	
	    	String cleanName = "Name";
	    	String cleanMotto = "Motto";
	    	
	    	cleanName = ESAPI.validator().getValidSafeHTML("profileName", name, 16, true);
	    	cleanMotto = ESAPI.validator().getValidSafeHTML("profileMotto", motto, 32, true);
	    	
	    	cleanProfile = new ProfileDTO(profile.getId(), cleanName, cleanMotto, profile.getJoinDate());
    	}
    	
    	return cleanProfile;
	}

	private List<ProfileDTO> sanitizeProfiles(List<ProfileDTO> profileList)
			throws ValidationException {
		List<ProfileDTO> cleanList = new ArrayList<ProfileDTO>();

		for (ProfileDTO p : profileList) {
			ProfileDTO cleanProfile = sanitizeProfile(p);
			cleanList.add(cleanProfile);
		}

		return cleanList;
	}
	
	//Convert Post
	private PostDO postObjectToData(PostDTO post) {
		PostDO convertedPost = null;
		
		if (post != null)
		{
			convertedPost = new PostDO(post.getId(), post.getBlogId(), post.getTimestamp(), post.getTitle(), post.getText());
		}
		
		return convertedPost;
	}
	
	private PostDTO postDataToObject(PostDO post, int profileId) {
		PostDTO convertedPost = null;
		
		if (post != null)
		{
			convertedPost = new PostDTO(post.getId(), post.getBlogId(), profileId, post.getTimestamp(), post.getTitle(), post.getText());
		}
		
		return convertedPost;
	}

	//Convert Blog 
	private BlogDO blogObjectToData(BlogDTO blog) {
		BlogDO convertedBlog = null;
		
		if (blog != null)
		{
			convertedBlog = new BlogDO(blog.getId(), blog.getProfileId(), blog.getTitle(), blog.getDescription());
		}
		
		return convertedBlog;
	}
	
	private BlogDTO blogDataToDomain(BlogDO blog) {
		BlogDTO convertedBlog = null;
		
		if (blog != null)
		{
			convertedBlog = new BlogDTO(blog.getId(), blog.getProfileId(), blog.getTitle(), blog.getDescription());
		}
		
		return convertedBlog;
	}

	//Convert Profile
	private ProfileDO profileObjectToData(ProfileDTO profile) {
		ProfileDO convertedProfile = null;
		
		if (profile != null)
		{
			convertedProfile = new ProfileDO(profile.getId(), profile.getName(), profile.getMotto(), profile.getJoinDate());
		}
		
		return convertedProfile;
	}
	
	private ProfileDTO profileDataToObject(ProfileDO profile) {
		ProfileDTO convertedProfile = null;
		
		if (profile != null)
		{
			convertedProfile = new ProfileDTO(profile.getId(), profile.getName(), profile.getMotto(), profile.getJoinDate());
		}
		
		return convertedProfile;
	}
	
	//====Auth====//
	public boolean authenticateUser(String username, String password)
		throws InvalidInputException {
		boolean success = false;
		
		if ((username.equals("") || password.equals("")) &&
			(username == null || password == null))
		{
			throw new InvalidInputException("Invalid Credentials.");
		}
		
		int profileId = 0;
		AuthDO storedHash = null;
		
		try {
			profileId = dbProfile.getProfileByName(username).getId();
			storedHash = dbHash.getAuthById(profileId);
		}
		catch(SQLException e)
		{
			throw new InvalidInputException("Invalid Credentials.", e);
		}
		
		success = verifyHash(charsToBytes(password.toCharArray()), storedHash.getHash());
		
		return success;
	}
	
	private String hashPassword(byte[] password) 
			throws InvalidInputException {
		String hash = new String(hashWithSalt(password, getRandomSalt()));
		return hash;
	}
	
	private byte[] hashWithSalt(byte[] input, byte[] salt)
		throws InvalidInputException {
		byte[] saltedHash = new byte[32];

		if (input != null)
		{
			try {
				String hash = JavaEncryptor.getInstance().hash(new String(input), new String(salt));
				
				byte[] salted = salt;
				byte[] hashed = charsToBytes(hash.toCharArray());
				
				saltedHash = new byte[salted.length + hashed.length + 1];
				
				System.arraycopy(salted, 0, saltedHash, 0, salted.length);
				saltedHash[salted.length] = ':';
				System.arraycopy(hashed, 0, saltedHash, salted.length + 1, hashed.length);	
			} 
			catch (EncryptionException e) {
				throw new InvalidInputException(e);
			}
		}
		
		if (input == null || saltedHash == null)
		{
			throw new InvalidInputException("Authentication Error.");
		}

		return saltedHash;
	}

	private boolean verifyHash(byte[] input, String saltedHash)
			throws InvalidInputException {
		boolean success = false;
		
		byte[] raw = input;
		byte[] salt = charsToBytes(saltedHash.split(":")[0].toCharArray());
		
		String testHash = new String(hashWithSalt(raw, salt));
		
		if (testHash.trim().equals(saltedHash.trim()))
		{
			success = true;
		}
		
		return success;
	}
	
	private byte[] getRandomSalt() {
		String salt =  new BigInteger(132, random).toString(32);
		salt = salt.substring(0, 16);
		return charsToBytes(salt.toCharArray());
	}
	
    private byte[] charsToBytes(char[] chars){
        Charset charset = Charset.forName("UTF-8");
        ByteBuffer byteBuffer = charset.encode(CharBuffer.wrap(chars));
        return Arrays.copyOf(byteBuffer.array(), chars.length);
    }

    private char[] bytesToChars(byte[] bytes){
        Charset charset = Charset.forName("UTF-8");
        CharBuffer charBuffer = charset.decode(ByteBuffer.wrap(bytes));
        return Arrays.copyOf(charBuffer.array(), bytes.length);    
    }
}