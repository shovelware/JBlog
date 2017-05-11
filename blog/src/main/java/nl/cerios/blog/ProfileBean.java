package nl.cerios.blog;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

//@Stateless
public class ProfileBean {

	@Resource(name="jdbc/blog")
	private DataSource data;
	
	public void testFunc(){
		try {
			Context ctx =  new InitialContext();
			Context envCtx = (Context) ctx.lookup("java:comp/env");
			data = (DataSource) envCtx.lookup("jdbc/blog");
			
			Connection conn = data.getConnection();
			Statement sttmnt = conn.createStatement();
			
			String sqlQuery = "SELECT * FROM profile";
			
			ResultSet rslt = sttmnt.executeQuery(sqlQuery);
			
			while (rslt.next())
			{
				System.out.println(rslt.getString("name"));
			}
			
			System.out.println("aaaaa");
		} 
		
		catch (SQLException | NamingException e) {
			e.printStackTrace();
		}
	}

}
