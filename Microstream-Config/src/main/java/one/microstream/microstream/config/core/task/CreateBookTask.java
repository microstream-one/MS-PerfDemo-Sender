package one.microstream.microstream.config.core.task;

import one.microstream.microstream.config.core.BookstoreHttpClient;
import one.microstream.microstream.config.core.Executable;
import one.microstream.microstream.config.core.MockupDataGenerator;

public class CreateBookTask implements Executable
{
	private final MockupDataGenerator data;
	private final BookstoreHttpClient httpClient;

	public CreateBookTask(long rngSeed, BookstoreHttpClient httpClient)
	{
		this.data = new MockupDataGenerator(rngSeed);
		this.httpClient = httpClient;
	}

	@Override
	public void execute() throws Exception
	{
		httpClient.createBook(data.generateBook());
	}
}
