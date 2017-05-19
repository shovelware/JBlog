package nl.cerios.blog.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.yaml.snakeyaml.Yaml;

public class ConnectionFactory {

	private ConnectionConfiguration config;
	
	String driverClassName = "com.mysql.jdbc.Driver";
	String connectionUrl = "jdbc:mysql://localhost:3306/blog";
	
	//Replace these with yaml config when it works
	String dbUser = "root";
	String dbPwd = "w0rtel";

	private static ConnectionFactory connectionFactory = null;

	private ConnectionFactory() {
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
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
			System.out.println(e);
		}
	}

	public Connection getConnection() throws SQLException {
		Connection conn = null;
		conn = DriverManager.getConnection(connectionUrl, dbUser, dbPwd);
		return conn;
	}

	public static ConnectionFactory getInstance() {
		if (connectionFactory == null) {
			connectionFactory = new ConnectionFactory();
		}
		return connectionFactory;
	}

}
