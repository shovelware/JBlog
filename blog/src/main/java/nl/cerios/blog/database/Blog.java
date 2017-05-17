package nl.cerios.blog.database;

public final class Blog {
	private int id;
	private int profile_id;
	private String title;
	private String description;
	
	public Blog(int id, int profile_id, String title, String description){
		this.id = id;
		this.profile_id = profile_id;
		this.title = title;
		this.description = description;
	}
	
	public int getId() { return id; }
	public int getProfileId() { return profile_id; }
	public String getTitle() { return title; }
	public String getDescription() { return description; }
	
}
