
package one.microstream.microstream.config.rest;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import one.microstream.microstream.config.entities.Book;


public class MSBooks implements AutoCloseable
{
	private final Client    client;
	private final WebTarget webTarget;
	
	private static String URL = "http://3.67.234.72:8082/books/";

	public MSBooks()
	{
		this.client    = ClientBuilder.newClient();
		this.webTarget = this.client.target(MSBooks.URL);
	}

	public Book[] getAllBooks()
	{
		final Book[] response = this.webTarget.request(MediaType.APPLICATION_JSON).get(Book[].class);
		return response;
	}

	@Override
	public void close()
	{
		this.client.close();
	}
}
