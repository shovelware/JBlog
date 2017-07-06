package nl.cerios.clog.database;

public final class AuthDO {
	int id;
	String hash;
		
	public AuthDO(int id, String hash) {
		this.id = id;
		this.hash = hash;
	}
	
	public int getId() { return id; }
	public String getHash() { return hash; }
}
