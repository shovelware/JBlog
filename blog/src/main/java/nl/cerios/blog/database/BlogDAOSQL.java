package nl.cerios.blog.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BlogDAOSQL implements BlogDAO {

	@Override
	public BlogDTO getBlogById(int blogId) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		BlogDTO blog = null;

		try {
			connection = ConnectionFactory.getInstance().getConnection();

			statement = connection.prepareStatement(
					"SELECT * FROM blog" +
					" WHERE id=?"
					);

			statement.setInt(1, blogId);

			resultSet = statement.executeQuery();
			blog = GetFromResultSet(resultSet);

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}

		finally{
			Close(connection, statement, resultSet);
		}
		return blog;
	}

	@Override
	public List<BlogDTO> getBlogByProfileId(int profileId) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<BlogDTO> blog = new ArrayList<BlogDTO>();

		try {
			connection = ConnectionFactory.getInstance().getConnection();

			statement = connection.prepareStatement(
					"SELECT * FROM blog" +
					" WHERE profile_id=?"
					);

			statement.setInt(1, profileId);

			resultSet = statement.executeQuery();

			blog = PopulateFromResultSet(resultSet);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}


		finally{
			Close(connection, statement, resultSet);
		}
		return blog;
	}

	//When we get to search we'll get to this
	@Override
	public List<BlogDTO> getBlogByTitle(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean InsertBlog(BlogDTO newBlog) {
		Connection connection = null;
		PreparedStatement statement = null;
		boolean success = false;

		try {
			connection = ConnectionFactory.getInstance().getConnection();
			statement = connection.prepareStatement(
					"INSERT INTO blog (id, profile_id, title, description)" +
					" VALUES (?, ?, ?, ?);"
					);
			
			statement.setInt(1, newBlog.getId());
			statement.setInt(2, newBlog.getProfileId());
			statement.setString(3, newBlog.getTitle());
			statement.setString(4, newBlog.getDescription());

			System.out.println(statement.toString());
			
			statement.executeUpdate();
			
			success = true;

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}

		finally {
			Close(connection, statement, null);
		}
		
		return success;
	}

	//Updating comes later
	@Override
	public boolean UpdateBlog(BlogDTO updatedBlog) {
		// TODO Auto-generated method stub
		return false;
	}

	//Deletion comes later
	@Override
	public boolean DeleteBlog(int blogId) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private BlogDTO GetFromResultSet(ResultSet resultSet) {
		
		BlogDTO blog = null;
		
		//Convert each resultSet row to a PostDTO
		try {
			while (resultSet.next()) {
				blog = new BlogDTO(
						resultSet.getInt("id"),
						resultSet.getInt("profile_id"),
						resultSet.getString("title"),
						resultSet.getString("description"));
			}
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		
		return blog;
	}
	
	private List<BlogDTO> PopulateFromResultSet(ResultSet resultSet) {
		
		List<BlogDTO> blogs = new ArrayList<BlogDTO>();
		
		//Convert each resultSet row to a PostDTO
		try {
			while (resultSet.next()) {
				BlogDTO b = new BlogDTO(
						resultSet.getInt("id"),
						resultSet.getInt("profile_id"),
						resultSet.getString("title"),
						resultSet.getString("description"));

				blogs.add(b);
			}
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		
		return blogs;
	}

	private void Close(Connection connection, PreparedStatement statement, ResultSet resultSet) {
		try { if (resultSet != null) resultSet.close(); } catch (Exception e) {};
		try { if (statement != null) statement.close(); } catch (Exception e) {};
		try { if (connection != null) connection.close(); } catch (Exception e) {};
	}

}
