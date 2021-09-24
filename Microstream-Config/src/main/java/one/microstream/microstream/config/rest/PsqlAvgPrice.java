
package one.microstream.microstream.config.rest;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;


public class PsqlAvgPrice implements AutoCloseable
{
	private final Client    client;
	private final WebTarget webTarget;
	
	private static String URL = "http://3.121.12.21:8081/avgprice/";

	public PsqlAvgPrice()
	{
		this.client    = ClientBuilder.newClient();
		this.webTarget = this.client.target(PsqlAvgPrice.URL);
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
