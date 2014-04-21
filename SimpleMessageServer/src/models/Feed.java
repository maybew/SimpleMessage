package models;

public class Feed {
	private String author;
	private String content;
	private String datetime;
	
	public Feed(String author, String content, String datetime) {
		this.author = author;
		this.content = content;
		this.datetime = datetime;
	}

	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getDatetime() {
		return datetime;
	}
	
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
}
