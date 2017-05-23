package nl.cerios.blog.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostDAOSQL implements PostDAO {

	@Override
	public List<PostDTO> getPostById(int postId) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<PostDTO> posts = new ArrayList<PostDTO>();

		try {
			connection = ConnectionFactory.getInstance().getConnection();

			statement = connection.prepareStatement(
					"SELECT * FROM post" +
					" WHERE id=?"
					);

			statement.setInt(1, postId);

			resultSet = statement.executeQuery();

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}

		posts = PopulateFromResultSet(resultSet);

		return posts;
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
					" WHERE blog_id=?"
					);

			statement.setInt(1, blogId);

			resultSet = statement.executeQuery();

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}

		posts = PopulateFromResultSet(resultSet);

		return posts;
	}

	//Headers are for later
	@Override
	public List<PostDTO> getPostHeaderById(int postId) {
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

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}

		posts = PopulateFromResultSet(resultSet);

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
			statement.setInt(3, newPost.getBlogID());

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
	public boolean UpdatePost(PostDTO updatedPost) {
		// TODO Auto-generated method stub
		return false;
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

}
