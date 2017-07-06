package nl.cerios.clog.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Statement;

public class ProfileDAOSQL implements ProfileDAO {

	@Override
	public ProfileDO getProfileById(int profileId)
			throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		ProfileDO profile = null;

		try {
			connection = ConnectionFactory.getInstance().getConnection();

			statement = connection.prepareStatement(
					"SELECT * FROM profile" +
					" WHERE id=?"
					);

			statement.setInt(1, profileId);

			resultSet = statement.executeQuery();

			profile = GetFromResultSet(resultSet);
		}		
		finally { 
			Close(connection, statement, resultSet); 
		}
		
		return profile;
	}
	
	public ProfileDO getProfileByName(String profileName)
			throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		ProfileDO profile = null;

		try {
			connection = ConnectionFactory.getInstance().getConnection();

			statement = connection.prepareStatement(
					"SELECT * FROM profile" +
					" WHERE name=?"
					);

			statement.setString(1, profileName);

			resultSet = statement.executeQuery();

			profile = GetFromResultSet(resultSet);
		}		
		finally { 
			Close(connection, statement, resultSet); 
		}
		
		return profile;
	}

	@Override
	public int insertProfile(ProfileDO newProfile)
			throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		int newProfileId = 0;

		try {
			connection = ConnectionFactory.getInstance().getConnection();
			statement = connection.prepareStatement(
					"INSERT INTO profile (name, motto)" +
					" VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS
					);
			
			statement.setString(1, newProfile.getName());
			statement.setString(2, newProfile.getMotto());

			statement.executeUpdate();

			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
			  newProfileId = resultSet.getInt(1);
			}
		}
		finally {
			Close(connection, statement, null);
		}
		
		return newProfileId;
	}

	@Override
	public int updateProfile(ProfileDO updatedProfile)
			throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		int updatedProfileId = 0;

		try {
			connection = ConnectionFactory.getInstance().getConnection();
			statement = connection.prepareStatement(
					"UPDATE profile " +
					" SET motto = ?" + 
					" WHERE id = ?"
					);
			
			statement.setString(1, updatedProfile.getMotto());
			statement.setInt(2, updatedProfile.getId());

			statement.executeUpdate();

			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
			  updatedProfileId = resultSet.getInt(1);
			}
		}
		finally {
			Close(connection, statement, null);
		}
		
		return updatedProfileId;
	}

	@Override
	public boolean deleteProfile(int profileId)
			throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		boolean success = false;
		
		try {
			connection = ConnectionFactory.getInstance().getConnection();
			statement = connection.prepareStatement(
					"DELETE FROM profile " +
					" WHERE id = ?"
					);
			
			statement.setInt(1, profileId);

			int affectedRows = statement.executeUpdate();
			success = (affectedRows == 1);
		}
		finally {
			Close(connection, statement, null);
		}
		
		return success;
	}
	
	private List<ProfileDO> PopulateFromResultSet(ResultSet resultSet)
			throws SQLException {
		List<ProfileDO> profiles = new ArrayList<ProfileDO>();
		
		while (resultSet.next()) {
			ProfileDO p = new ProfileDO(
					resultSet.getInt("id"),
					resultSet.getString("name"),
					resultSet.getString("motto"),
					resultSet.getTimestamp("joindate").toLocalDateTime());

			profiles.add(p);
		}
		
		return profiles;
	}

	private ProfileDO GetFromResultSet(ResultSet resultSet)
			throws SQLException {
		ProfileDO profile = null;
		
		while (resultSet.next()) {
			profile = new ProfileDO(
					resultSet.getInt("id"),
					resultSet.getString("name"),
					resultSet.getString("motto"),
					resultSet.getTimestamp("joindate").toLocalDateTime());
		}
		
		return profile;
	}

	private void Close(Connection connection, PreparedStatement statement, ResultSet resultSet)
			throws SQLException {
		if (resultSet != null) { resultSet.close(); }
		if (statement != null) { statement.close(); }
		if (connection != null) { connection.close(); }
	}
}
