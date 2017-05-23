package nl.cerios.blog.database;

import java.util.List;

public interface BlogDAO {
	List<BlogDTO> getBlogById(int blogId);
	List<BlogDTO> getBlogByProfileId(int profileId);
	
	List<BlogDTO> getBlogByTitle(String title);
	
	boolean InsertBlog(BlogDTO newBlog);
	boolean UpdateBlog(BlogDTO updatedBlog);	
	boolean DeleteBlog(int blogId);
}
