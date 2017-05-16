package nl.cerios.blog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PostFetcher {

	public ArrayList<Post> fetchRecentPosts(int count) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		ArrayList<Post> postList = new ArrayList<Post>();

		try {
			// THIS METHOD IS INSECURE!
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost/blog?" + "user=root&password=w0rtel");
			
			statement = connection.prepareStatement(
					"SELECT * FROM post "
					+ "ORDER BY TIMESTAMP DESC "
					+ "LIMIT ? ");
			statement.setInt(1, count);
			
			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				Post p = new Post(
						resultSet.getInt("id"), 
						resultSet.getInt("blog_id"),
						resultSet.getTimestamp("timestamp").toLocalDateTime(),
						resultSet.getString("title"),
						resultSet.getString("text")
						);
						
				postList.add(p);
			}
			
			return postList;
		
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}

		finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}

				if (statement != null) {
					statement.close();
				}

				if (connection != null) {
					connection.close();
				}
				
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		
	}

}
