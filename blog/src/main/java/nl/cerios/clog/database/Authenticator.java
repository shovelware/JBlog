package nl.cerios.clog.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Authenticator {

	public boolean AuthenticateUser(String username, String password){
		boolean authenticated = false;

		int id = GetIdByName(username);
		String dbPassword = null;
		
		if (id != 0)
		{
			dbPassword = GetPasswordById(id);
			if (dbPassword != null && password.equals(dbPassword)) {
				authenticated = true;
			}
		}
		
		return authenticated;
	}
	
	public boolean InsertPassword(String username, String password)
	{
		boolean success = false;
	
		int id = GetIdByName(username);
		
		if (id != 0)
		{
			success = InsertPasswordById(id, password);
		}
		
		return success;
	}
	
	public int GetIdByName(String username){
		int id = 0;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try{
			connection = ConnectionFactory.getInstance().getConnection();
			
			//Perform a lookup of the user in the profile db
			statement = connection.prepareStatement(
					"SELECT id FROM profile" +
					" WHERE name=?"
					);

			statement.setString(1, username);
			resultSet = statement.executeQuery();
			
			if (resultSet.next()) {
				id = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		
		finally{
			Close(connection, statement, resultSet);
		}
		return id;
	}
	
	private String GetPasswordById(int id) {
		String dbPassword = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try{
			connection = ConnectionFactory.getInstance().getConnection();
			
			//Perform a lookup of the user in the profile db
			statement = connection.prepareStatement(
					"SELECT password FROM auth" +
					" WHERE profile_id=?"
					);

			statement.setInt(1, id);
			resultSet = statement.executeQuery();
			
			if (resultSet.next()) {
				dbPassword = resultSet.getString(1);
			}

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		finally{
			Close(connection, statement, resultSet);
		}
		return dbPassword;
	}

	private boolean InsertPasswordById(int id, String password) {
		boolean success = false;
	
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
	
		try{
			connection = ConnectionFactory.getInstance().getConnection();
		
			//Perform a lookup of the user in the profile db
			statement = connection.prepareStatement(
					"INSERT INTO auth (profile_id, password)" +
					" VALUES(?, ?)"
					);

			statement.setInt(1,  id);
			statement.setString(2,  password);
			
			statement.executeUpdate();
			
			success = true;

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		finally{
			Close(connection, statement, resultSet);
		}
		
		return success;
	}

	
	private void Close(Connection connection, PreparedStatement statement, ResultSet resultSet) {
		try { if (resultSet != null) resultSet.close(); } catch (Exception e) {};
		try { if (statement != null) statement.close(); } catch (Exception e) {};
		try { if (connection != null) connection.close(); } catch (Exception e) {};
	}
	
}
