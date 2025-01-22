package one.microstream.microstream.config.core.task;

import java.util.List;

import one.microstream.microstream.config.core.BookstoreHttpClient;
import one.microstream.microstream.config.core.Executable;
import one.microstream.microstream.config.dto.DTOBook;

public class AdminCreateBooksTask implements Executable
{
	private final List<List<DTOBook>> books;
	private final BookstoreHttpClient httpClient;

	public AdminCreateBooksTask(BookstoreHttpClient httpClient, List<List<DTOBook>> books)
	{
		this.httpClient = httpClient;
		this.books = books;
	}

	@Override
	public void execute()
	{					
		for (List<DTOBook> books : books)
		{
			httpClient.createBookBatched(books);
		}
	}
}
