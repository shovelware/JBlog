package nl.cerios.clog.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AuthDAOSQL implements AuthDAO {

	@Override
	public AuthDO getAuthById(int authId)
			throws SQLException {	
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		AuthDO auth = null;

		try {
			connection = ConnectionFactory.getInstance().getConnection();

			statement = connection.prepareStatement(
					"SELECT * FROM hash" +
					" WHERE id=?"
					);

			statement.setInt(1, authId);

			resultSet = statement.executeQuery();
			auth = GetFromResultSet(resultSet);

		}
		finally{
			Close(connection, statement, resultSet);
		}
		
		return auth;
	}

	@Override
	public boolean insertAuth(AuthDO newAuth)
			throws SQLException {		
		Connection connection = null;
		PreparedStatement statement = null;
		boolean success = false;

		try {
			connection = ConnectionFactory.getInstance().getConnection();
			statement = connection.prepareStatement(
					"INSERT INTO hash (id, salthash)" +
					" VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS
					);
			
			statement.setInt(1, newAuth.getId());
			statement.setString(2, newAuth.getHash());
			
			int affectedRows = statement.executeUpdate();
			success = (affectedRows == 1);
		}
		finally {
			Close(connection, statement, null);
		}
		
		return success;
	}

	@Override
	public boolean updateAuth(AuthDO updatedAuth)
			throws SQLException {		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		boolean success = false;

		try {
			connection = ConnectionFactory.getInstance().getConnection();
			statement = connection.prepareStatement(
					"UPDATE hash " +
					" SET salthash = ?" + 
					" WHERE id = ?", Statement.RETURN_GENERATED_KEYS
					);
			
			statement.setString(1, updatedAuth.getHash());
			statement.setInt(2, updatedAuth.getId());
			
			int affectedRows = statement.executeUpdate();
			success = (affectedRows == 1);
		}
		finally {
			Close(connection, statement, resultSet);
		}
		
		return success;
	}

	@Override
	public boolean deleteAuth(int authId)
			throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		boolean success = false;
		
		try {
			connection = ConnectionFactory.getInstance().getConnection();
			statement = connection.prepareStatement(
					"DELETE FROM hash " +
					" WHERE id = ?"
					);
			
			statement.setInt(1, authId);
			
			int affectedRows = statement.executeUpdate();
			success = (affectedRows == 1);
		}
		finally {
			Close(connection, statement, null);
		}
		
		return success;
	}
	
	private AuthDO GetFromResultSet(ResultSet resultSet)
			throws SQLException {
		AuthDO auth= null;
		
		while (resultSet.next()) {
			auth = new AuthDO(
					resultSet.getInt("id"),
					resultSet.getString("salthash"));
		}
		
		return auth;
	}
	
	private void Close(Connection connection, PreparedStatement statement, ResultSet resultSet)
			throws SQLException {
		if (resultSet != null) { resultSet.close(); } 
		if (statement != null) { statement.close(); }
		if (connection != null) { connection.close(); } 
	}
}
