
package one.microstream.microstream.config.entities;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Author
{
	@JsonProperty("name")
	private String name;
	
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
