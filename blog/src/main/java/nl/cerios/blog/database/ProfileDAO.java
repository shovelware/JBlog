package nl.cerios.blog.database;

import java.time.LocalDateTime;
import java.util.ArrayList;

public interface ProfileDAO {
	ArrayList<ProfileDTO> getProfileById(int profileId);
	ArrayList<ProfileDTO> getProfileByPostId(int postId);
	ArrayList<ProfileDTO> getProfileByBlogId(int blogId);
	
	ArrayList<ProfileDTO> getProfileByName(String name);
	ArrayList<ProfileDTO> getProfileByTimestamp(LocalDateTime since, int count);
	
	boolean InsertProfile(ProfileDTO newProfile);
	boolean UpdateProfile(ProfileDTO updatedProfile);	
	boolean DeleteProfile(int ProfileId);
}