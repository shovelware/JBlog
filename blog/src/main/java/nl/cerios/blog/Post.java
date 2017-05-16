package nl.cerios.blog;

import java.time.LocalDateTime;

public class Post {
	private int id;
	private int blog_id;
	private LocalDateTime timestamp;
	private String title;
	private String text;
	
	public Post(int id, int blog_id, LocalDateTime timestamp, String title, String text)
	{
		this.id = id;
		this.blog_id = id;
		this.timestamp = timestamp;
		this.title = title;
		this.text = text;
	}
	
	public int getID() { return id; }
	public int getBlogID() { return blog_id; }
	public LocalDateTime getTimestamp() { return timestamp; }
	public String getTitle() { return title; }
	public String getText() { return text; }
}
