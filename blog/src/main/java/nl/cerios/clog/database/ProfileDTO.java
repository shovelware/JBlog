package nl.cerios.clog.database;

import java.time.LocalDateTime;

public final class ProfileDTO {
	private int id;
	private String name;
	private String motto;
	private LocalDateTime joinDate;

	public ProfileDTO(int id, String name, String motto, LocalDateTime joinDate) {
		this.id = id;
		this.name = name;
		this.motto = motto;
		this.joinDate = joinDate;
	}

	public int getId() { return id;	}
	public String getName() { return name; }
	public String getMotto() { return motto; }
	public LocalDateTime getJoinDate() { return joinDate; }
}