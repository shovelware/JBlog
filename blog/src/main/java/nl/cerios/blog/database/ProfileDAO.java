package nl.cerios.blog.database;

import java.time.LocalDateTime;
import java.util.List;

public interface ProfileDAO {
	List<ProfileDTO> getProfileById(int profileId);
	List<ProfileDTO> getProfileByPostId(int postId);
	List<ProfileDTO> getProfileByBlogId(int blogId);
	
	List<ProfileDTO> getProfileByName(String name);
	List<ProfileDTO> getProfileByTimestamp(LocalDateTime since, int count);
	
	boolean InsertProfile(ProfileDTO newProfile);
	boolean UpdateProfile(ProfileDTO updatedProfile);	
	boolean DeleteProfile(int ProfileId);
}