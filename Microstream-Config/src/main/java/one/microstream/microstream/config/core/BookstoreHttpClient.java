package one.microstream.microstream.config.core;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import one.microstream.microstream.config.dto.DTOAuthor;
import one.microstream.microstream.config.dto.DTOBook;

public class BookstoreHttpClient implements AutoCloseable
{
	private final Client client = ClientBuilder.newClient();
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final WebTarget rootTarget;

	public BookstoreHttpClient(final String rootUrl)
	{
		this.rootTarget = client.target(rootUrl);
		this.objectMapper.findAndRegisterModules();
		this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}

	public void createBook(final DTOBook book)
	{
		request(book, "/books", "PUT");
	}

	@SuppressWarnings("unchecked")
	public List<DTOBook> searchByTitle(final String title)
	{
		return retrieve(null, "/books/search/" + URLEncoder.encode(title, StandardCharsets.UTF_8), "GET", List.class);
	}

	@SuppressWarnings("unchecked")
	public List<DTOBook> searchByAuthor(final String author)
	{
		return retrieve(
			null,
			"/books/search/author/" + URLEncoder.encode(author, StandardCharsets.UTF_8),
			"GET",
			List.class
		);
	}

	@SuppressWarnings("unchecked")
	public List<DTOAuthor> listAuthors(final int limit)
	{
		return retrieve(
			null,
			"/authors",
			"GET",
			List.class,
			Collections.singletonMap("limit", Integer.toString(limit))
		);
	}

	private <T> T retrieve(Object dto, String path, String method, Class<T> clazz)
	{
		return retrieve(dto, path, method, clazz, Collections.emptyMap());
	}

	private <T> T retrieve(Object dto, String path, String method, Class<T> clazz, Map<String, String> queryParams)
	{
		final var response = request(dto, path, method, queryParams);
		try
		{
			return this.objectMapper.readValue(response.readEntity(String.class), clazz);
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
