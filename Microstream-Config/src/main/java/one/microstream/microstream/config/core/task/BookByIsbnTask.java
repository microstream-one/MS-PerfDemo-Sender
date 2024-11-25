package one.microstream.microstream.config.core.task;

import java.util.List;
import java.util.Random;

import one.microstream.microstream.config.core.BookstoreHttpClient;
import one.microstream.microstream.config.core.Executable;
import one.microstream.microstream.config.dto.DTOBook;

public class BookByIsbnTask implements Executable
{
	private final Random random;
	private final BookstoreHttpClient httpClient;
	private final List<DTOBook> books;

	public BookByIsbnTask(long rngSeed, BookstoreHttpClient httpClient, List<DTOBook> books)
	{
		this.random = new Random(rngSeed);
		this.httpClient = httpClient;
		this.books = books;
	}

	@Override
	public void execute() throws Exception
	{
		httpClient.searchByIsbn(books.get(random.nextInt(books.size())).isbn());
	}
}
