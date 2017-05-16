package nl.cerios.blog;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ProfileBean {

	private DataSource data;
	
	public void testFunc(){
		
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		
		try {
			
			//THIS METHOD IS INSECURE, FIX IT!
			Class.forName("com.mysql.jdbc.Driver");			
			connection = DriverManager.getConnection("jdbc:mysql://localhost/blog?" + "user=root&password=w0rtel"); 

			//BUT THIS ONE DOESN'T WORK, FIX IT TOO!
//			Context ctx =  new InitialContext();
//			Context envCtx = (Context) ctx.lookup("java:comp/env");
//			data = (DataSource) envCtx.lookup("jdbc/blog");
//			Connection conn = data.getConnection();
//			
			statement = connection.createStatement();
			
			System.out.println("Opened connection");
			
			String sqlQuery = "SELECT * FROM profile";
			
			resultSet = statement.executeQuery(sqlQuery);
			

			System.out.println("Executed Query");
			
			while (resultSet.next())
			{
				System.out.println(resultSet.getString("name"));
			}
			
		} 
		
		catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		finally
		{
			if (connection != null)
			{
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
						System.err.println(e);
			        }
			}
		}
	}

}
