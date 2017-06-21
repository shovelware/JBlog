package nl.cerios.clog.database.v2;

import java.time.LocalDateTime;

public final class PostDTO {
	private int id;
	private int blogId;
	private LocalDateTime timestamp;
	private String title;
	private String text;
	
	public PostDTO(int id, int blog_id, LocalDateTime timestamp, String title, String text)
	{
		this.id = id;
		this.blogId = blog_id;
		this.timestamp = timestamp;
		this.title = title;
		this.text = text;
	}
	
	public int getId() { return id; }
	public int getBlogId() { return blogId; }
	public LocalDateTime getTimestamp() { return timestamp; }
	public String getTitle() { return title; }
	public String getText() { return text; }
}