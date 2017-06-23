package nl.cerios.clog.database.v2;

import java.sql.SQLException;

public interface ProfileDAO {
	ProfileDTO getProfileById(int profileId) throws SQLException;
	
	int insertProfile(ProfileDTO newProfile) throws SQLException;
	int updateProfile(ProfileDTO updatedProfile) throws SQLException;	
	boolean deleteProfile(int profileId) throws SQLException;
}