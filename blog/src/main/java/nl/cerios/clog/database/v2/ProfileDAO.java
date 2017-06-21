package nl.cerios.clog.database.v2;

public interface ProfileDAO {
	ProfileDTO getProfileById(int profileId);
	
	int InsertProfile(ProfileDTO newProfile);
	int UpdateProfile(ProfileDTO updatedProfile);	
	boolean DeleteProfile(int profileId);
}