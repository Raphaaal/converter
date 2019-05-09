package email;

import java.util.Date;

public class Attachment implements Comparable<Attachment> {
	
	private String name;
	private String sender;
	private Date date;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	public Attachment(String name, String sender, Date date) {
		this.name = name;
		this.sender = sender;
		this.date = date;
	}
	
	@Override
	public String toString() {
		return "Attachment [name=" + name + ", sender=" + sender + ", date=" + date + "]";
	}
	@Override
	public int compareTo(Attachment attachment) {
		if (this.date.before(attachment.getDate()))
			return -1;
		else
			return 1;
				
	}
	
}
