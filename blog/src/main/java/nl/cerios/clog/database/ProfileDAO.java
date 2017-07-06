package nl.cerios.clog.database;

import java.sql.SQLException;

public interface ProfileDAO {
	ProfileDO getProfileById(int profileId) throws SQLException;
	
	int insertProfile(ProfileDO newProfile) throws SQLException;
	int updateProfile(ProfileDO updatedProfile) throws SQLException;	
	boolean deleteProfile(int profileId) throws SQLException;
}