
package one.microstream.microstream.config.core.threads;

import java.util.TimerTask;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import one.microstream.microstream.config.entities.Book;


public class TaskBookQuery extends TimerTask implements AutoCloseable
{
	private final Client    client;
	private final WebTarget webTarget;
	private final String    URL = "";
	
	public TaskBookQuery()
	{
		this.client    = ClientBuilder.newClient();
		this.webTarget = this.client.target(this.URL);
	}
	
	@Override
	public void run()
	{
		this.getAllBooks();
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
