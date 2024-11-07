package one.microstream.microstream.config.core;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.javaparser.utils.Log;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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

	private <T> List<DTOBook> retrieve(final Object dto, final String path, final String method, final Class<T> clazz)
	{
		final var response = request(dto, path, method);
		try
		{
			final var entity = response.readEntity(String.class);
			LoggerFactory.getLogger(getClass()).info("BODY IS={}", entity);
			return (List<DTOBook>)this.objectMapper.readValue(entity, clazz);
		}
		catch (JsonProcessingException e)
		{
			throw new RuntimeException("Failed to parse response", e);
		}
	}

	private Response request(final Object dto, final String path, final String method)
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

		final Response response = this.rootTarget.path(path).request(MediaType.APPLICATION_JSON).method(method, entity);

		final int status = response.getStatus();
		if (status != 200 && status != 404 && status != 500)
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
