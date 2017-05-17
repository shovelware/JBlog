package nl.cerios.blog.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.yaml.snakeyaml.Yaml;

public class PostFetcher {

	private ConnectionConfiguration config;
	
	public PostFetcher() {
		
		Yaml yaml = new Yaml();
		
		try
		{
			File yamlConfig = new File("config.yml");
			//System.out.println(yamlConfig.getAbsolutePath());
			
			InputStream input = new FileInputStream(yamlConfig);
			
			config = yaml.loadAs(input,  ConnectionConfiguration.class);

			input.close();
		}
		
		catch (Exception e)
		{
			//System.out.println(e);
		}
	}
	
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
					"SELECT * FROM post" +
					" ORDER BY TIMESTAMP DESC" +
					" LIMIT ?"
					);
			
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

	public void addPost(int user, int blog, String title, String text)
	{
		Connection connection = null;
		PreparedStatement statement = null;
		

		try {
			// THIS METHOD IS INSECURE!
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost/blog?" + "user=root&password=w0rtel");

			statement = connection.prepareStatement(
					"INSERT INTO post (title, text, blog_id)" +
					" VALUES (?, ?, ?);"
					);
			
					statement.setString(1, title);
					statement.setString(2, text);
					statement.setInt(3, blog);
					
					statement.executeUpdate();
					
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}

		//Close SQL connection
		finally {
			try {
				if (statement != null) { statement.close(); }
				if (connection != null) { connection.close(); }
				
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		
	}
}
