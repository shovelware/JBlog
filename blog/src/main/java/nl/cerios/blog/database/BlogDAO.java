package nl.cerios.blog.database;

import java.util.ArrayList;

public interface BlogDAO {
	ArrayList<BlogDTO> getBlogById(int blogId);
	ArrayList<BlogDTO> getBlogByProfileId(int profileId);
	
	ArrayList<BlogDTO> getBlogByTitle(String title);
	
	boolean InsertBlog(BlogDTO newBlog);
	boolean UpdateBlog(BlogDTO updatedBlog);	
	boolean DeleteBlog(int blogId);
}
