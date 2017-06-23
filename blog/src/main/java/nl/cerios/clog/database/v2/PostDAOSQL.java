package nl.cerios.clog.database.v2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class PostDAOSQL implements PostDAO {

	@Override
	public PostDTO getPostById(int postId)
		throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		PostDTO post = null;

		try {
			connection = ConnectionFactory.getInstance().getConnection();

			statement = connection.prepareStatement(
					"SELECT * FROM post" +
					" WHERE id=?"
					);

			statement.setInt(1, postId);
			resultSet = statement.executeQuery();
			
			post = GetFromResultSet(resultSet);
		}
		finally {
			Close(connection, statement, resultSet);
		}
		
		return post;
	}

	@Override
	public List<PostDTO> getPostsByBlogId(int blogId, int count)
			throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<PostDTO> posts = new ArrayList<PostDTO>();

		try {
			connection = ConnectionFactory.getInstance().getConnection();

			statement = connection.prepareStatement(
					"SELECT * FROM post" +
					" WHERE blog_id=?" +
					" ORDER BY timestamp DESC" +
					" LIMIT ?"
					);

			statement.setInt(1, blogId);
			statement.setInt(2,  count);
			resultSet = statement.executeQuery();
			
			posts = PopulateFromResultSet(resultSet);
		}
		finally {
			Close(connection, statement, resultSet);
		}
		
		return posts;
	}

	@Override
	public List<PostDTO> getPostsSinceTimestamp(LocalDateTime since, int count)
			throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<PostDTO> posts = new ArrayList<PostDTO>();

		try {
			connection = ConnectionFactory.getInstance().getConnection();

			statement = connection.prepareStatement(
					"SELECT * FROM post" +
					" WHERE timestamp >= ?" +
					" ORDER BY timestamp DESC" +
					" LIMIT ?"
					);

			statement.setTimestamp(1, Timestamp.valueOf(since));
			statement.setInt(2, count);
			resultSet = statement.executeQuery();

			posts = PopulateFromResultSet(resultSet);
		}
		finally {
			Close(connection, statement, resultSet);			
		}
		
		return posts;
	}

	public List<PostDTO> getPostsRecent(int count)
			throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<PostDTO> posts = new ArrayList<PostDTO>();

		try {
			connection = ConnectionFactory.getInstance().getConnection();

			statement = connection.prepareStatement(
					"SELECT * FROM post" +
					" ORDER BY timestamp DESC" +
					" LIMIT ?"
					);

			statement.setInt(1, count);
			resultSet = statement.executeQuery();

			posts = PopulateFromResultSet(resultSet);
		}
		finally {
			Close(connection, statement, resultSet);			
		}
		
		return posts;
	}
	
	@Override
	public int insertPost(PostDTO newPost)
			throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		int newPostId = 0;

		try {
			connection = ConnectionFactory.getInstance().getConnection();
			statement = connection.prepareStatement(
					"INSERT INTO post (title, text, blog_id)" +
					" VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS
					);
			
			statement.setString(1, newPost.getTitle());
			statement.setString(2, newPost.getText());
			statement.setInt(3, newPost.getBlogId());
			statement.executeUpdate();

			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
			  newPostId = resultSet.getInt(1);
			}
		}
		finally {
			Close(connection, statement, resultSet);
		}
		
		return newPostId;
	}

	@Override
	public int updatePost(PostDTO updatedPost)
			throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		int updatedPostId = 0;

		try {
			connection = ConnectionFactory.getInstance().getConnection();
			statement = connection.prepareStatement(
					"UPDATE post " +
					" SET title = ?, text = ?" + 
					" WHERE id = ?", Statement.RETURN_GENERATED_KEYS
					);
			
			statement.setString(1, updatedPost.getTitle());
			statement.setString(2, updatedPost.getText());
			statement.setInt(3, updatedPost.getId());
			
			statement.executeUpdate();
			
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
			  updatedPostId = resultSet.getInt(1);
			}
		}
		finally {
			Close(connection, statement, resultSet);
		}
		
		return updatedPostId;
	}

	@Override
	public boolean deletePost(int postId)
			throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		boolean success = false;
		
		try {
			connection = ConnectionFactory.getInstance().getConnection();
			statement = connection.prepareStatement(
					"DELETE FROM post " +
					" WHERE id = ?"
					);
			
			statement.setInt(1, postId);
			
			statement.execute();
			success = true;

		}
		finally {
			Close(connection, statement, null);
		}
		
		return success;
	}
	
	private PostDTO GetFromResultSet(ResultSet resultSet)
			throws SQLException	{
		PostDTO post = null;
		
		if (resultSet.next()) {
			post = new PostDTO(
				resultSet.getInt("id"),
				resultSet.getInt("blog_id"),
				resultSet.getTimestamp("timestamp").toLocalDateTime(),
				resultSet.getString("title"),
				resultSet.getString("text"));
		}
		
		return post;
	}
	
	private List<PostDTO> PopulateFromResultSet(ResultSet resultSet)
			throws SQLException {
		
		List<PostDTO> posts = new ArrayList<PostDTO>();
		
		
		//Convert each resultSet row to a PostDTO
		while (resultSet.next()) {
			PostDTO p = new PostDTO(
				resultSet.getInt("id"),
				resultSet.getInt("blog_id"),
				resultSet.getTimestamp("timestamp").toLocalDateTime(),
				resultSet.getString("title"),
				resultSet.getString("text"));
			posts.add(p);
		}
			
		return posts;
	}

	private void Close(Connection connection, PreparedStatement statement, ResultSet resultSet)
			throws SQLException {
		if (resultSet != null) { resultSet.close(); }
		if (statement != null) { statement.close(); }
		if (connection != null) { connection.close(); }
	}
}

