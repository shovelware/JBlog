package nl.cerios.blog.database;

public class ConnectionConfiguration {
	private String username;
	private String password;
	private int postLimit;
	
	public ConnectionConfiguration(String username, String password, int postLimit)
	{
		this.username = username;
		this.password = password;
		this.postLimit = postLimit;
	}
	
	public String GetUsername() { return username; }
	public String GetPassword() { return password; }
	public int GetPostsToRetreive() { return postLimit; }
	
	public void SetUsername(String username) { this.username = username; }
	public void SetPassword(String password) { this.password = password; }
	public void SetPostLimit(int postLimit) { this.postLimit = postLimit; }
}
