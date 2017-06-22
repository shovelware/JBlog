package nl.cerios.clog.database.v2;

public interface ProfileDAO {
	ProfileDTO getProfileById(int profileId);
	
	int insertProfile(ProfileDTO newProfile);
	int updateProfile(ProfileDTO updatedProfile);	
	boolean deleteProfile(int profileId);
}