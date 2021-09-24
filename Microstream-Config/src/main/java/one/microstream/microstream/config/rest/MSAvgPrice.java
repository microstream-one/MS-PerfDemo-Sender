
package one.microstream.microstream.config.rest;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;


public class MSAvgPrice implements AutoCloseable
{
	private final Client    client;
	private final WebTarget webTarget;

	private static String URL = "http://3.67.234.72:8082/avgprice/";
	
	public MSAvgPrice()
	{
		this.client    = ClientBuilder.newClient();
		this.webTarget = this.client.target(MSAvgPrice.URL);
	}
	
	public Double getAvgBookPrice()
	{
		final Double response = this.webTarget.request(MediaType.APPLICATION_JSON).get(Double.class);
		return response;
	}
	
	@Override
	public void close()
	{
		this.client.close();
	}
}
