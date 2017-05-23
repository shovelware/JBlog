package nl.cerios.blog.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProfileDAOSQL implements ProfileDAO {

	@Override
	public List<ProfileDTO> getProfileById(int profileId) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<ProfileDTO> profiles = new ArrayList<ProfileDTO>();

		try {
			connection = ConnectionFactory.getInstance().getConnection();

			statement = connection.prepareStatement(
					"SELECT * FROM profile" +
					" WHERE id=?"
					);

			statement.setInt(1, profileId);

			resultSet = statement.executeQuery();

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}

		profiles = PopulateFromResultSet(resultSet);

		return profiles;
	}

	//Unimplemented
	@Override
	public List<ProfileDTO> getProfileByPostId(int postId) {
		// TODO Auto-generated method stub
		return null;
	}

	//Unimplemented
	@Override
	public List<ProfileDTO> getProfileByBlogId(int blogId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProfileDTO> getProfileByName(String name) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<ProfileDTO> profiles = new ArrayList<ProfileDTO>();

		try {
			connection = ConnectionFactory.getInstance().getConnection();

			statement = connection.prepareStatement(
					"SELECT * FROM profile" +
					" WHERE name=?"
					);

			statement.setString(1, name);

			resultSet = statement.executeQuery();

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}

		profiles = PopulateFromResultSet(resultSet);

		return profiles;
	}

	//Unimplemented (Needed?)
	@Override
	public List<ProfileDTO> getProfileByTimestamp(LocalDateTime since, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean InsertProfile(ProfileDTO newProfile) {

		Connection connection = null;
		PreparedStatement statement = null;
		boolean success = false;

		try {
			connection = ConnectionFactory.getInstance().getConnection();
			statement = connection.prepareStatement(
					"INSERT INTO profile (name, motto)" +
					" VALUES (?, ?);"
					);
			
			statement.setString(1, newProfile.getName());
			statement.setString(2, newProfile.getMotto());

			System.out.println(statement.toString());
			
			statement.executeUpdate();
			
			success = true;

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}

		return success;
	}

	//Updating comes later
	@Override
	public boolean UpdateProfile(ProfileDTO updatedProfile) {
		// TODO Auto-generated method stub
		return false;
	}

	//Deleting comes later
	@Override
	public boolean DeleteProfile(int ProfileId) {
		// TODO Auto-generated method stub
		return false;
	}
	
private List<ProfileDTO> PopulateFromResultSet(ResultSet resultSet) {
		
		List<ProfileDTO> profiles = new ArrayList<ProfileDTO>();
		
		//Convert each resultSet row to a PostDTO
		try {
			while (resultSet.next()) {
				ProfileDTO p = new ProfileDTO(
						resultSet.getInt("id"),
						resultSet.getString("name"),
						resultSet.getString("motto"),
						resultSet.getTimestamp("joindate").toLocalDateTime());

				profiles.add(p);
			}
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		
		return profiles;
	}

}
