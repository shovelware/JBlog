package nl.cerios.clog.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BlogDAOSQL implements BlogDAO {

	@Override
	public BlogDO getBlogById(int blogId)
			throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		BlogDO blog = null;

		try {
			connection = ConnectionFactory.getInstance().getConnection();

			statement = connection.prepareStatement(
					"SELECT * FROM blog" +
					" WHERE id=?"
					);

			statement.setInt(1, blogId);

			resultSet = statement.executeQuery();
			blog = GetFromResultSet(resultSet);

		}
		finally{
			Close(connection, statement, resultSet);
		}
		
		return blog;
	}

	@Override
	public List<BlogDO> getBlogsByProfileId(int profileId)
			throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<BlogDO> blogs = new ArrayList<BlogDO>();

		try {
			connection = ConnectionFactory.getInstance().getConnection();

			statement = connection.prepareStatement(
					"SELECT * FROM blog" +
					" WHERE profile_id=?"
					);

			statement.setInt(1, profileId);

			resultSet = statement.executeQuery();

			blogs = PopulateFromResultSet(resultSet);
		}
		finally{
			Close(connection, statement, resultSet);
		}
		return blogs;
	}
	
	@Override
	public List<Integer> getBlogIdsByProfileId(int profileId)
			throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<Integer>blogIds = new ArrayList<Integer>();

		try {
			connection = ConnectionFactory.getInstance().getConnection();

			statement = connection.prepareStatement(
					"SELECT id FROM blog" +
					" WHERE profile_id=?"
					);

			statement.setInt(1, profileId);

			resultSet = statement.executeQuery();
			
			while(resultSet.next())
			{
				blogIds.add(resultSet.getInt("id"));
			}
		}
		finally{
			Close(connection, statement, resultSet);
		}
		
		return blogIds;		
	}

	public int getProfileIdByBlogId(int id)
			throws SQLException	{		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		int profileId = 0;
		
		try {
			connection = ConnectionFactory.getInstance().getConnection();

			statement = connection.prepareStatement(
					"SELECT profile_id FROM blog" +
					" WHERE id=?"
					);

			statement.setInt(1, id);

			resultSet = statement.executeQuery();

			if (resultSet.next())
			{
				profileId = resultSet.getInt("profile_id");
			}
		} 
		catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		finally{
			Close(connection, statement, resultSet);
		}
		
		return profileId;
	}
	
	@Override
	public int insertBlog(BlogDO newBlog)
			throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		int newBlogId = 0;

		try {
			connection = ConnectionFactory.getInstance().getConnection();
			statement = connection.prepareStatement(
					"INSERT INTO blog (profile_id, title, description)" +
					" VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS
					);
			
			statement.setInt(1, newBlog.getProfileId());
			statement.setString(2, newBlog.getTitle());
			statement.setString(3, newBlog.getDescription());
			
			statement.executeUpdate();
			
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
			  newBlogId = resultSet.getInt(1);
			}
		}
		finally {
			Close(connection, statement, resultSet);
		}
		
		return newBlogId;
	}

	@Override
	public int updateBlog(BlogDO updatedBlog)
			throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		int updatedBlogId = 0;

		try {
			connection = ConnectionFactory.getInstance().getConnection();
			statement = connection.prepareStatement(
					"UPDATE blog " +
					" SET title = ?, description = ?" + 
					" WHERE id = ?", Statement.RETURN_GENERATED_KEYS
					);
			
			statement.setString(1, updatedBlog.getTitle());
			statement.setString(2, updatedBlog.getDescription());
			statement.setInt(3, updatedBlog.getId());
			
			statement.executeUpdate();

			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
			  updatedBlogId = resultSet.getInt(1);
			}
		}
		finally {
			Close(connection, statement, resultSet);
		}
		
		return updatedBlogId;
	}

	@Override
	public boolean deleteBlog(int blogId)
			throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		boolean success = false;
		
		try {
			connection = ConnectionFactory.getInstance().getConnection();
			statement = connection.prepareStatement(
					"DELETE FROM blog " +
					" WHERE id = ?"
					);
			
			statement.setInt(1, blogId);

			int affectedRows = statement.executeUpdate();
			success = (affectedRows == 1);
		}
		finally {
			Close(connection, statement, null);
		}
		
		return success;
	}

	private BlogDO GetFromResultSet(ResultSet resultSet)
			throws SQLException {
		BlogDO blog = null;
		
		while (resultSet.next()) {
			blog = new BlogDO(
					resultSet.getInt("id"),
					resultSet.getInt("profile_id"),
					resultSet.getString("title"),
					resultSet.getString("description"));
		}
		
		return blog;
	}
	
	private List<BlogDO> PopulateFromResultSet(ResultSet resultSet)
			throws SQLException {
		List<BlogDO> blogs = new ArrayList<BlogDO>();
		
		while (resultSet.next()) {
			BlogDO b = new BlogDO(
				resultSet.getInt("id"),
				resultSet.getInt("profile_id"),
				resultSet.getString("title"),
				resultSet.getString("description"));
			blogs.add(b);
		}
		
		return blogs;
	}

	private void Close(Connection connection, PreparedStatement statement, ResultSet resultSet)
			throws SQLException {
		if (resultSet != null) { resultSet.close(); } 
		if (statement != null) { statement.close(); }
		if (connection != null) { connection.close(); } 
	}
}
