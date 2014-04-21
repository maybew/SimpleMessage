package models;

public class Subscribe {
	private String reader;
	private String author;
	private String datetime;
	
	public Subscribe(String reader, String author, String datetime) {
		this.reader = reader;
		this.author = author;
		this.datetime = datetime;
	}

	public String getReader() {
		return reader;
	}
	
	public void setReader(String reader) {
		this.reader = reader;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getDatetime() {
		return datetime;
	}
	
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
}
