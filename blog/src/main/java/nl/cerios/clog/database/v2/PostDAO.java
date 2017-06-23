package nl.cerios.clog.database.v2;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

interface PostDAO {
	PostDTO getPostById(int postId) throws SQLException;
	
	List<PostDTO> getPostsByBlogId(int blogId, int count) throws SQLException;

	List<PostDTO> getPostsSinceTimestamp(LocalDateTime since, int count) throws SQLException;
	
	int insertPost(PostDTO newPost) throws SQLException;
	int updatePost(PostDTO updatedPost) throws SQLException;	
	boolean deletePost(int postId) throws SQLException;
}