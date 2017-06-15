package nl.cerios.blog.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostDAOSQL implements PostDAO {

	@Override
	public PostDTO getPostById(int postId) {

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

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}

		finally {
			Close(connection, statement, resultSet);
		}
		return post;
	}

	//Unimplemented
	@Override
	public List<PostDTO> getPostByAuthorId(int authorId) {
		// TODO Auto-generated method stub
		//How to get from profileid through blog to post
		//Or posts should know their author probably?
		return null;
	}

	@Override
	public List<PostDTO> getPostByBlogId(int blogId) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<PostDTO> posts = new ArrayList<PostDTO>();

		try {
			connection = ConnectionFactory.getInstance().getConnection();

			statement = connection.prepareStatement(
					"SELECT * FROM post" +
					" WHERE blog_id=?" +
					" ORDER BY TIMESTAMP DESC"
					);

			statement.setInt(1, blogId);

			resultSet = statement.executeQuery();
			posts = PopulateFromResultSet(resultSet);

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}

		finally {
			Close(connection, statement, resultSet);
		}
		return posts;
	}

	//Headers are for later
	@Override
	public PostDTO getPostHeaderById(int postId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//Headers are for later
	@Override
	public List<PostDTO> getPostHeaderByAuthorId(int authorId) {
		// TODO Auto-generated method stub
		return null;
	}

	//Headers are for later
	@Override
	public List<PostDTO> getPostHeaderByBlogId(int blogId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PostDTO> getPostByTimestamp(LocalDateTime since, int count) {

		// !Since does not do anything yet! (From/To maybe?)

		// Variables
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<PostDTO> posts = new ArrayList<PostDTO>();

		try {
			connection = ConnectionFactory.getInstance().getConnection();

			statement = connection.prepareStatement(
					"SELECT * FROM post" +
					" ORDER BY TIMESTAMP DESC" +
					" LIMIT ?"
					);

			statement.setInt(1, count);

			resultSet = statement.executeQuery();

			posts = PopulateFromResultSet(resultSet);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}

		finally {
			Close(connection, statement, resultSet);			
		}
		return posts;
	}

	@Override
	public boolean InsertPost(PostDTO newPost) {

		Connection connection = null;
		PreparedStatement statement = null;
		boolean success = false;

		try {
			connection = ConnectionFactory.getInstance().getConnection();
			statement = connection.prepareStatement(
					"INSERT INTO post (title, text, blog_id)" +
					" VALUES (?, ?, ?);"
					);
			
			statement.setString(1, newPost.getTitle());
			statement.setString(2, newPost.getText());
			statement.setInt(3, newPost.getBlogId());
			
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
	public boolean UpdatePost(PostDTO updatedPost) {
		Connection connection = null;
		PreparedStatement statement = null;
		boolean success = false;

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

			System.out.println(statement.toString());
			
			statement.executeUpdate();
			
			///Return post id
			ResultSet rs = statement.getGeneratedKeys();
			if (rs.next()) {
			  int newPostId = rs.getInt(1);
			}
			
			success = true;

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		
		finally {
			Close(connection, statement, null);
		}
		return success;
	}

	//Deleting comes later
	@Override
	public boolean DeletePost(int postId) {
		// TODO Auto-generated method stub
		return false;
	}

	private List<PostDTO> PopulateFromResultSet(ResultSet resultSet) {
		
		List<PostDTO> posts = new ArrayList<PostDTO>();
		
		//Convert each resultSet row to a PostDTO
		try {
			while (resultSet.next()) {
				PostDTO p = new PostDTO(
						resultSet.getInt("id"),
						resultSet.getInt("blog_id"),
						resultSet.getTimestamp("timestamp").toLocalDateTime(),
						resultSet.getString("title"),
						resultSet.getString("text"));

				posts.add(p);
			}
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		
		return posts;
	}

	//Takes the first result from the set and returns it as a DTO
	private PostDTO GetFromResultSet(ResultSet resultSet)
	{
		PostDTO post = null;
		
		try {
			if (resultSet.next()) {
				post = new PostDTO(
						resultSet.getInt("id"),
						resultSet.getInt("blog_id"),
						resultSet.getTimestamp("timestamp").toLocalDateTime(),
						resultSet.getString("title"),
						resultSet.getString("text"));
			}
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		
		return post;
	}
	
	private void Close(Connection connection, PreparedStatement statement, ResultSet resultSet) {
		try { if (resultSet != null) resultSet.close(); } catch (Exception e) {};
		try { if (statement != null) statement.close(); } catch (Exception e) {};
		try { if (connection != null) connection.close(); } catch (Exception e) {};
	}
}
