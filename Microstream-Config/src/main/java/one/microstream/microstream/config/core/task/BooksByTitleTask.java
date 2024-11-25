package one.microstream.microstream.config.core.task;

import java.util.Random;

import one.microstream.microstream.config.core.BookstoreHttpClient;
import one.microstream.microstream.config.core.Executable;

public class BooksByTitleTask implements Executable
{
	private final Random random;
	private final BookstoreHttpClient httpClient;

	public BooksByTitleTask(long rngSeed, BookstoreHttpClient httpClient)
	{
		this.random = new Random(rngSeed);
		this.httpClient = httpClient;
	}

	@Override
	public void execute() throws Exception
	{
		httpClient.searchByTitle(Character.toString((char)random.nextInt('a', 'z' + 1)));
	}
}
