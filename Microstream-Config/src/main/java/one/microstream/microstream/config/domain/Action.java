
package one.microstream.microstream.config.domain;

import java.time.LocalDateTime;


public class Action
{
	private String        description;
	private LocalDateTime date;

	public Action(final LocalDateTime date, final String description)
	{
		super();
		this.description = description;
		this.date        = date;
	}
	
	public String getDescription()
	{
		return this.description;
	}

	public void setDescription(final String description)
	{
		this.description = description;
	}

	public LocalDateTime getDate()
	{
		return this.date;
	}

	public void setDate(final LocalDateTime date)
	{
		this.date = date;
	}

}
