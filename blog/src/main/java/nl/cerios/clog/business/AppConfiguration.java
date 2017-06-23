package nl.cerios.clog.business;

public class AppConfiguration {
	private String url;
	private String username;
	private String password;
	private int postsToRetreive;
	
	public AppConfiguration()
	{
		
	}
	
	public void init(String url, String username, String password, int postsToRetreive)
	{
		this.url = url;
		this.username = username;
		this.password = password;
		this.postsToRetreive = postsToRetreive;
	}
	
	public String getUrl() { return url; }
	public String getUsername() { return username; }
	public String getPassword() { return password; }
	public int getPostsToRetrieve() { return postsToRetreive; }
	
	public void setUrl(String url) { this.url = url; }
	public void setUsername(String username) { this.username = username; }
	public void setPassword(String password) { this.password = password; }
	public void setPostsToRetreive(int postsToRetreive) { this.postsToRetreive = postsToRetreive; }
}
