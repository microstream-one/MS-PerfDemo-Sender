
package one.microstream.microstream.config.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;


public class BookInShop
{
	@JsonSerialize
	private Book       book;
	@JsonSerialize
	private List<Shop> shops = new ArrayList<>();

	public Book getBook()
	{
		return this.book;
	}
	
	public void setBook(final Book book)
	{
		this.book = book;
	}
	
	public List<Shop> getShops()
	{
		return this.shops;
	}
	
	public void setShops(final List<Shop> shops)
	{
		this.shops = shops;
	}
	
}
