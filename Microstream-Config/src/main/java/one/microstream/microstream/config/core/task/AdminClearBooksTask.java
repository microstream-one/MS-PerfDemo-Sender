package one.microstream.microstream.config.core.task;

import one.microstream.microstream.config.core.BookstoreHttpClient;
import one.microstream.microstream.config.core.Executable;

public class AdminClearBooksTask implements Executable
{
	private final BookstoreHttpClient httpClient;

	public AdminClearBooksTask(BookstoreHttpClient httpClient)
	{
		this.httpClient = httpClient;
	}

	@Override
	public void execute()
	{
		httpClient.clearBooks();
	}
}
