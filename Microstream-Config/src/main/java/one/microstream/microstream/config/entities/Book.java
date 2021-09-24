
package one.microstream.microstream.config.entities;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Book
{
	@JsonProperty("isbn13")
	private String isbn13;
	
	@JsonProperty("title")
	private String title;
	
	@JsonProperty("author")
	private Author author;

	@JsonProperty("name")
	private String name;
	
	@JsonProperty("isbn13")
	public String getIsbn13()
	{
		return this.isbn13;
	}
	
	@JsonProperty("isbn13")
	public void setIsbn13(final String isbn13)
	{
		this.isbn13 = isbn13;
	}
	
	@JsonProperty("title")
	public String getTitle()
	{
		return this.title;
	}
	
	@JsonProperty("title")
	public void setTitle(final String title)
	{
		this.title = title;
	}
	
	@JsonProperty("author")
	public Author getAuthor()
	{
		return this.author;
	}
	
	@JsonProperty("author")
	public void setAuthor(final Author author)
	{
		this.author = author;
	}
	
	@JsonProperty("name")
	public String getName()
	{
		return this.name;
	}
	
	@JsonProperty("name")
	public void setName(final String name)
	{
		this.name = name;
	}
	
}
