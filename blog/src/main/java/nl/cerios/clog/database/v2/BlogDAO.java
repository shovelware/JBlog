package nl.cerios.clog.database.v2;

import java.sql.SQLException;
import java.util.List;

public interface BlogDAO {
	BlogDTO getBlogById(int blogId) throws SQLException;
	
	List<BlogDTO> getBlogsByProfileId(int profileId) throws SQLException;
	List<Integer> getBlogIdsByProfileId(int profileId) throws SQLException;
	
	int insertBlog(BlogDTO newBlog) throws SQLException;
	int updateBlog(BlogDTO updatedBlog) throws SQLException;	
	boolean deleteBlog(int blogId) throws SQLException;
}
