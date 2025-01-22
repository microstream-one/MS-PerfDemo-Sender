package one.microstream.microstream.config.ui.model;

import com.rapidclipse.framework.server.resources.Caption;

import one.microstream.microstream.config.dto.DTOBook;

public class Book
{
	private String isbn;
	private String title;
	private String publicationDate;
	private int edition;
	private int availableQuantity;
	private double price;
	private String authorID;
	private String publisherID;

	public Book()
	{

	}

	public Book(DTOBook dto)
	{
		this.isbn = dto.isbn();
		this.title = dto.title();
		this.publicationDate = dto.publicationDate().toString();
		this.edition = dto.edition();
		this.availableQuantity = dto.availableQuantity();
		this.price = dto.price();
		this.authorID = dto.author().firstname() + " " + dto.author().lastname();
		this.publicationDate = dto.publisher().company();
	}

	@Caption("ISBN")
	public String getIsbn()
	{
		return isbn;
	}

	public void setIsbn(String isbn)
	{
		this.isbn = isbn;
	}

	@Caption("Title")
	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	@Caption("Published")
	public String getPublicationDate()
	{
		return publicationDate;
	}

	public void setPublicationDate(String publicationDate)
	{
		this.publicationDate = publicationDate;
	}

	@Caption("Edition")
	public int getEdition()
	{
		return edition;
	}

	public void setEdition(int edition)
	{
		this.edition = edition;
	}

	@Caption("Quantity")
	public int getAvailableQuantity()
	{
		return availableQuantity;
	}

	public void setAvailableQuantity(int availableQuantity)
	{
		this.availableQuantity = availableQuantity;
	}

	@Caption("Price")
	public double getPrice()
	{
		return price;
	}

	public void setPrice(double price)
	{
		this.price = price;
	}

	@Caption("AuthorID")
	public String getAuthorID()
	{
		return authorID;
	}

	public void setAuthorID(String authorID)
	{
		this.authorID = authorID;
	}

	@Caption("PublisherID")
	public String getPublisherID()
	{
		return publisherID;
	}

	public void setPublisherID(String publisherID)
	{
		this.publisherID = publisherID;
	}

}
