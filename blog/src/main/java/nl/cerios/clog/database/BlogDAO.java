package nl.cerios.clog.database;

import java.sql.SQLException;
import java.util.List;

public interface BlogDAO {
	BlogDO getBlogById(int blogId) throws SQLException;
	
	List<BlogDO> getBlogsByProfileId(int profileId) throws SQLException;
	List<Integer> getBlogIdsByProfileId(int profileId) throws SQLException;
	
	int insertBlog(BlogDO newBlog) throws SQLException;
	int updateBlog(BlogDO updatedBlog) throws SQLException;	
	boolean deleteBlog(int blogId) throws SQLException;
}
