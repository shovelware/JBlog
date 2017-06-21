package nl.cerios.clog.database.v2;

import java.time.LocalDateTime;
import java.util.List;

interface PostDAO {
	PostDTO getPostById(int postId);
	
	List<PostDTO> getPostsByBlogId(int blogId, int count);

	List<PostDTO> getPostsSinceTimestamp(LocalDateTime since, int count);
	
	int InsertPost(PostDTO newPost);
	int UpdatePost(PostDTO updatedPost);	
	boolean DeletePost(int postId);
}