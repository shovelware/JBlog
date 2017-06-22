package nl.cerios.clog.database.v2;

import java.util.List;

public interface BlogDAO {
	BlogDTO getBlogById(int blogId);
	
	List<BlogDTO> getBlogsByProfileId(int profileId);
	List<Integer> getBlogIdsByProfileId(int profileId);
	
	int insertBlog(BlogDTO newBlog);
	int updateBlog(BlogDTO updatedBlog);	
	boolean deleteBlog(int blogId);
}
