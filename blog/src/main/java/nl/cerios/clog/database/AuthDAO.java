package nl.cerios.clog.database;

import java.sql.SQLException;

public interface AuthDAO {
	AuthDO getAuthById(int authId) throws SQLException;
		
	boolean insertAuth(AuthDO newAuth) throws SQLException;
	boolean updateAuth(AuthDO updatedAuth) throws SQLException;
	boolean deleteAuth(int authId) throws SQLException;
}
