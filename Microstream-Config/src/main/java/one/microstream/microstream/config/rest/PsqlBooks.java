
package one.microstream.microstream.config.rest;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import one.microstream.microstream.config.entities.Book;


public class PsqlBooks implements AutoCloseable
{
	private final Client    client;
	private final WebTarget webTarget;
	
	private static String URL = "http://3.121.12.21:8081/books/";

	public PsqlBooks()
	{
		this.client    = ClientBuilder.newClient();
		this.webTarget = this.client.target(PsqlBooks.URL);
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
