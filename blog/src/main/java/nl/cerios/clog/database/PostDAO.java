package nl.cerios.clog.database;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

interface PostDAO {
	PostDO getPostById(int postId) throws SQLException;
	
	List<PostDO> getPostsByBlogId(int blogId, int count) throws SQLException;

	List<PostDO> getPostsSinceTimestamp(LocalDateTime since, int count) throws SQLException;
	
	int insertPost(PostDO newPost) throws SQLException;
	int updatePost(PostDO updatedPost) throws SQLException;	
	boolean deletePost(int postId) throws SQLException;
}