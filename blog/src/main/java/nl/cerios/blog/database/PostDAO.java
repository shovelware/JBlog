package nl.cerios.blog.database;

import java.time.LocalDateTime;
import java.util.ArrayList;

interface PostDAO {
	
	ArrayList<PostDTO> getPostById(int postId);
	ArrayList<PostDTO> getPostByAuthorId(int authorId);
	ArrayList<PostDTO> getPostByBlogId(int blogId);
	
	//These functions don't copy body text, for reducing overhead
	ArrayList<PostDTO> getPostHeaderById(int postId);
	ArrayList<PostDTO> getPostHeaderByAuthorId(int authorId);
	ArrayList<PostDTO> getPostHeaderByBlogId(int blogId);

	ArrayList<PostDTO> getPostByTimestamp(LocalDateTime since, int count);
	
	boolean InsertPost(PostDTO newPost);
	boolean UpdatePost(PostDTO updatedPost);	
	boolean DeletePost(int postId);
}