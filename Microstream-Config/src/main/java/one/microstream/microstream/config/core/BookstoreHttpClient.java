package one.microstream.microstream.config.core;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import one.microstream.microstream.config.dto.DTOBook;

public class BookstoreHttpClient implements AutoCloseable
{
	private static final TypeReference<DTOBook> BOOK_TYPE = new TypeReference<>()
	{
	};
	private static final TypeReference<List<DTOBook>> BOOK_LIST_TYPE = new TypeReference<>()
	{
	};

	private final Client client = ClientBuilder.newClient();
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final WebTarget rootTarget;

	public BookstoreHttpClient(final String rootUrl)
	{
		this.rootTarget = client.target(rootUrl);
		this.objectMapper.findAndRegisterModules();
		this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		this.objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
	}

	public void createBook(final DTOBook book)
	{
		request(book, "/books", "PUT");
	}

	public void createBookBatched(final List<DTOBook> books)
	{
		request(books, "/books/batch", "PUT");
	}

	public void clearBooks()
	{
		request(null, "/books", "DELETE");
	}

	public List<DTOBook> searchByTitle(final String title)
	{
		return retrieve(
			null,
			"/books/search/" + URLEncoder.encode(title, StandardCharsets.UTF_8),
			"GET",
			BOOK_LIST_TYPE
		);
	}

	public DTOBook searchByIsbn(final String isbn)
	{
		return retrieve(null, "/books/" + URLEncoder.encode(isbn, StandardCharsets.UTF_8), "GET", BOOK_TYPE);
	}

	public List<DTOBook> searchByAuthor(final String author)
	{
		return retrieve(
			null,
			"/books/search/author/" + URLEncoder.encode(author, StandardCharsets.UTF_8),
			"GET",
			BOOK_LIST_TYPE
		);
	}

	private <T> T retrieve(Object dto, String path, String method, TypeReference<T> type)
	{
		return retrieve(dto, path, method, type, Collections.emptyMap());
	}

	private <T> T retrieve(
		Object dto,
		String path,
		String method,
		TypeReference<T> type,
		Map<String, String> queryParams
	)
	{
		final var response = request(dto, path, method, queryParams);
		try
		{
			return this.objectMapper.readValue(response.readEntity(String.class), type);
		}
		catch (JsonProcessingException e)
		{
			throw new RuntimeException("Failed to parse response", e);
		}
		finally
		{
			response.close();
		}
	}

	private void request(Object dto, String path, String method)
	{
		request(dto, path, method, Collections.emptyMap()).close();
	}

	private Response request(Object dto, String path, String method, Map<String, String> queryParams)
	{
		final Entity<Object> entity;
		if (dto == null)
		{
			entity = null;
		}
		else
		{
			try
			{
				entity = Entity.json(objectMapper.writeValueAsString(dto));
			}
			catch (JsonProcessingException e)
			{
				throw new RuntimeException("Failed to convert book to json", e);
			}
		}

		WebTarget target = this.rootTarget.path(path);
		for (final var entry : queryParams.entrySet())
		{
			target = target.queryParam(entry.getKey(), entry.getValue());
		}
		final Response response = target.request(MediaType.APPLICATION_JSON).method(method, entity);

		final int status = response.getStatus();
		if (status != 200)
		{
			final var body = response.readEntity(String.class);
			throw new RuntimeException(
				String.format("Http Request failed with status %s and body %s", status, body == null ? "" : body)
			);
		}

		return response;
	}

	@Override
	public void close()
	{
		this.client.close();
	}
}
