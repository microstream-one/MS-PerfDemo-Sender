package one.microstream.microstream.config.core.task;

import one.microstream.microstream.config.core.BookstoreHttpClient;
import one.microstream.microstream.config.core.Executable;

public class ResetInsertBookPMTask implements Executable
{
	private final BookstoreHttpClient httpClient;
	
	public ResetInsertBookPMTask(BookstoreHttpClient httpClient)
	{
		this.httpClient = httpClient;
	}
	
	@Override
	public void execute() throws Exception
	{
		httpClient.resetBooksInsertPM();
	}

}
