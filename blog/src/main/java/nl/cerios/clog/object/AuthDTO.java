package nl.cerios.clog.object;

public final class AuthDTO {
	String name;
	String hash;
		
	public AuthDTO(String name, String hash) {
		this.name = name;
		this.hash = hash;
	}
	
	public String getName() { return name; } 
	public String getHash() { return hash; }
}
