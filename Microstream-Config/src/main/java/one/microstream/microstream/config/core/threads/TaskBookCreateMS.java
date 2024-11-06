
package one.microstream.microstream.config.core.threads;

import java.util.TimerTask;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;


public class TaskBookCreateMS extends TimerTask implements AutoCloseable
{
	private final Client    client;
	private final WebTarget webTarget;
	private final String    URL = "";

	public TaskBookCreateMS()
	{
		this.client    = ClientBuilder.newClient();
		this.webTarget = this.client.target(this.URL);
	}

	@Override
	public void run()
	{

	}
	
	@Override
	public void close()
	{
		this.client.close();
	}

}
