package nl.cerios.blog.database;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

interface PostDAO {
	
	PostDTO getPostById(int postId);
	List<PostDTO> getPostByAuthorId(int authorId);
	List<PostDTO> getPostByBlogId(int blogId);
	
	//These functions don't copy body text, for reducing overhead
	PostDTO getPostHeaderById(int postId);
	List<PostDTO> getPostHeaderByAuthorId(int authorId);
	List<PostDTO> getPostHeaderByBlogId(int blogId);

	List<PostDTO> getPostByTimestamp(LocalDateTime since, int count);
	
	boolean InsertPost(PostDTO newPost);
	boolean UpdatePost(PostDTO updatedPost);	
	boolean DeletePost(int postId);
}