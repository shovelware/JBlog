package nl.cerios.clog.domain;

public class BlogDO {
	private int id;
	private int profileId;
	private String title;
	private String description;
	
	public BlogDO(int id, int profile_id, String title, String description){
		this.id = id;
		this.profileId = profile_id;
		this.title = title;
		this.description = description;
	}
	
	public int getId() { return id; }
	public int getProfileId() { return profileId; }
	public String getTitle() { return title; }
	public String getDescription() { return description; }
}
