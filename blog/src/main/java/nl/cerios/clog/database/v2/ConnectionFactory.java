package nl.cerios.clog.database.v2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import nl.cerios.clog.business.AppConfiguration;

public class ConnectionFactory {

	private String username;
	private String password;
	private String url;
	
	String driverClassName = "com.mysql.jdbc.Driver";

	private static ConnectionFactory connectionFactory = null;

	private ConnectionFactory() {
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void init(String url, String username, String password)
	{
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		conn = DriverManager.getConnection(url + "?useSSL=false", username, password);
		return conn;
	}

	public static ConnectionFactory getInstance() {
		if (connectionFactory == null) {
			connectionFactory = new ConnectionFactory();
		}
		return connectionFactory;
	}

}
