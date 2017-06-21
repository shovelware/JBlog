package nl.cerios.clog.database.v2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Statement;

public class ProfileDAOSQL implements ProfileDAO {

	@Override
	public ProfileDTO getProfileById(int profileId) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		ProfileDTO profile = null;

		try {
			connection = ConnectionFactory.getInstance().getConnection();

			statement = connection.prepareStatement(
					"SELECT * FROM profile" +
					" WHERE id=?"
					);

			statement.setInt(1, profileId);

			resultSet = statement.executeQuery();

			profile = GetFromResultSet(resultSet);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		
		finally { 
			Close(connection, statement, resultSet); 
			}
		return profile;
	}

	@Override
	public int InsertProfile(ProfileDTO newProfile) {
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
		catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		finally {
			Close(connection, statement, null);
		}
		return newProfileId;
	}

	@Override
	public int UpdateProfile(ProfileDTO updatedProfile) {
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
		catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		finally {
			Close(connection, statement, null);
		}
		return updatedProfileId;
	}

	@Override
	public boolean DeleteProfile(int profileId) {
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
			
			statement.execute();
			success = true;

		}
		catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		finally {
			Close(connection, statement, null);
		}
		
		return success;
	}
	
	private List<ProfileDTO> PopulateFromResultSet(ResultSet resultSet) {
		List<ProfileDTO> profiles = new ArrayList<ProfileDTO>();
		
		try {
			while (resultSet.next()) {
				ProfileDTO p = new ProfileDTO(
						resultSet.getInt("id"),
						resultSet.getString("name"),
						resultSet.getString("motto"),
						resultSet.getTimestamp("joindate").toLocalDateTime());

				profiles.add(p);
			}
		}
		catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		
		return profiles;
	}

	private ProfileDTO GetFromResultSet(ResultSet resultSet) {
		ProfileDTO profile = null;
		
		try {
			while (resultSet.next()) {
				profile = new ProfileDTO(
						resultSet.getInt("id"),
						resultSet.getString("name"),
						resultSet.getString("motto"),
						resultSet.getTimestamp("joindate").toLocalDateTime());
			}
		}
		catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		
		return profile;
	}

	private void Close(Connection connection, PreparedStatement statement, ResultSet resultSet) {
		try { if (resultSet != null) resultSet.close(); } catch (SQLException e) { e.printStackTrace(); };
		try { if (statement != null) statement.close(); } catch (SQLException e) { e.printStackTrace(); };
		try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); };
	}
}
