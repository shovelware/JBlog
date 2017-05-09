package nl.cerios.blog;

import java.util.Date;

public class Profile {
	String name = "Shuzo Matsuoka";
	String motto = "NEBA GIB APPU!";
	String bio = "Unexpected hero of the internet, Shuzo's life changed when footage of him offering general encouragement while fishing for crabs knee-deep in the Atlantic Sea went viral around 2006-2008. He's since revelled in his fame, using his platform to spread more messages of endurance, heart and goodwill to the whole world.";
	
	//evt. add multi links
	String link = "https://youtu.be/KxGRhd_iWuE";
	
	//Some sort of pictures, plus avatar
	//img selfie = "Shuzo.png"
	
	Date joinDate = new Date(1299678577);

	public String getName() { return name; }
	public String getMotto() { return motto; }
	public String getBio(){	return bio;	}
	public Date getJoinDate() { return joinDate; }
	
	//evt. Somehow differentiate between links
	public String getLink() { return link; }
}