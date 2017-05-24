package nl.cerios.blog.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import nl.cerios.blog.AppConfiguration;

public class ConnectionFactory {

	private AppConfiguration config;
	
	String driverClassName = "com.mysql.jdbc.Driver";

	private static ConnectionFactory connectionFactory = null;

	private ConnectionFactory() {
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void init(AppConfiguration config)
	{
		this.config = config;
	}
	
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		conn = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
		return conn;
	}

	public static ConnectionFactory getInstance() {
		if (connectionFactory == null) {
			connectionFactory = new ConnectionFactory();
		}
		return connectionFactory;
	}

}
