package nl.cerios.clog.business;

import nl.cerios.clog.database.v2.PostDAOSQL;
import nl.cerios.clog.database.v2.BlogDAOSQL;
import nl.cerios.clog.database.v2.ProfileDAOSQL;

import nl.cerios.clog.database.v2.ConnectionFactory;
import nl.cerios.clog.database.Authenticator;

import nl.cerios.clog.ui.AppConfiguration;

import java.io.FileNotFoundException;

public class ClogLogic {

	ProfileDAOSQL dbProfile;
	BlogDAOSQL dbBlog;
	PostDAOSQL dbPost;
	
	//TODO: Implement images
	//ImageDAOSQL dbPic;
	
	Authenticator authenticator;
	
	AppConfiguration config;
	int postPagination = 5;
	
	//TODO: Config app here
	//public void init(AppConfiguration appConfig) throws FileNotFoundException {
	
	//====Post====//
	//TODO: Select Post
	//public PostDO getPost(int id) throws DataNotFoundException
	//public list<PostDO> getPostsRecent(int count) 
	//public list<PostDO> getPostsByBlog(int blogId, int count) throws DataNotFoundException
	//public list<PostDO> getPostsByBlogSince(int blogId, ZonedDateTime since) throws DataNotFoundException//????
	
	//TODO: Insert Post [Continue editing on invalid save try]
	//public int insertPost(PostDO post, ProfileDO user) throws InvalidInputException, AuthenticationException
	//public bool updatePost(PostDO post, ProfileDO user) throws InvalidInputException, AuthenticationException
	
	//TODO: Clean Post
	//public PostDO sanitize(PostDO)
	//public PostDTO sanitize(PostDTO)
	
	//TODO: Convert Post [Do we need to?]
	
	//====Blog====//
	//TODO: Select Blog
	//public BlogDO getBlog(int id) throws DataNotFoundException
	//public BlogDO getBlogByPost(int postId) throws DataNotFoundException
	//public list<BlogDO> getBlogsByProfile(int profileId) throws DataNotFoundException
	
	//TODO: Insert Blog
	//public int insertBlog(BlogDO blog, ProfileDO user) throws InvalidInputException, AuthenticationException
	//public bool updateBlog(BlogDO blog, ProfileDO user) throws InvalidInputException, AuthenticationException
	
	//TODO: Clean Blog
	//public BlogDO sanitize(BlogDO)
	//public BlogDTO sanitize(BlogDTO)
	
	//TODO: Convert Blog [Do we need to?]
	
	//====Profile====//
	//TODO: Select Profile
	//public ProfileDO getProfile(int id) throws DataNotFoundException
	//public ProfileDO getProfileByBlog(int blogId) throws DataNotFoundException
	//public ProfileDO getProfileByPost(int postID) throws DataNotFoundException
	
	//TODO: Insert new profile
	//public int insertProfile(ProfileDO profile, String password) throws InvalidInputException
	//public int updateProfile(ProfileDO profile, ProfileDO user) throws InvalidInputException, AuthenticationException
	
	//TODO: Clean Profile
	//public ProfileDO sanitize(ProfileDO)
	//public ProfileDTO sanitize(ProfileDTO)

	//TODO: Convert Profile [Do we need to?]
	
	//====Auth====//
	//public int checkPassword(String username, String password) throws InvalidInputException, AuthenticationException
	//public bool insertPassword(ProfileDO profile, String password) throws InvalidInputException, AuthenticationException
}
