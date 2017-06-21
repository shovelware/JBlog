package nl.cerios.clog.database.v2;

import java.util.List;

public interface BlogDAO {
	BlogDTO getBlogById(int blogId);
	
	List<BlogDTO> getBlogsByProfileId(int profileId);
	List<Integer> getBlogIdsByProfileId(int profileId);
	
	int InsertBlog(BlogDTO newBlog);
	int UpdateBlog(BlogDTO updatedBlog);	
	boolean DeleteBlog(int blogId);
}
