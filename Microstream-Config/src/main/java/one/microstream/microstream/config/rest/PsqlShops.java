
package one.microstream.microstream.config.rest;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import one.microstream.microstream.config.entities.BookInShop;


public class PsqlShops implements AutoCloseable
{
	private final Client    client;
	private final WebTarget webTarget;

	private static String URL = "http://3.121.12.21:8081/bookinshops/";
	
	public PsqlShops()
	{
		this.client    = ClientBuilder.newClient();
		this.webTarget = this.client.target(PsqlShops.URL);
	}
	
	public BookInShop[] getBookInShop()
	{
		final BookInShop[] response = this.webTarget.request(MediaType.APPLICATION_JSON).get(BookInShop[].class);
		return response;
	}
	
	@Override
	public void close()
	{
		this.client.close();
	}
}
