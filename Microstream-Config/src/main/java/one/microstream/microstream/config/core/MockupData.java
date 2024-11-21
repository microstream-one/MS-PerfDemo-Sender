
package one.microstream.microstream.config.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import one.microstream.microstream.config.dto.DTOAddress;
import one.microstream.microstream.config.dto.DTOAuthor;
import one.microstream.microstream.config.dto.DTOBook;
import one.microstream.microstream.config.dto.DTOPublisher;

public class MockupData
{
	private static final Logger LOG = LoggerFactory.getLogger(MockupData.class);
	private static final Path MOCKUP_DATA_PATH = Paths.get("./ms-perf-demo-data.json");

	private final List<DTOAuthor> authorsPool;
	private final List<DTOPublisher> publisherPool;

	private final Faker faker = new Faker(Locale.US);
	private final Random random = new Random();

	public static List<DTOBook> loadData() throws IOException
	{
		final var mapper = new ObjectMapper().findAndRegisterModules();
		return mapper.readValue(MOCKUP_DATA_PATH.toFile(), new TypeReference<List<DTOBook>>()
		{
		});
	}

	private MockupData(int authorPoolSize, int publisherPoolSize)
	{
		this.authorsPool = generatePool(authorPoolSize, this::generateAuthor);
		this.publisherPool = generatePool(publisherPoolSize, this::generatePublisher);
	}

	public DTOPublisher generatePublisher()
	{
		return new DTOPublisher(
			this.faker.internet().emailAddress(),
			this.faker.company().name(),
			this.generateAddresses(this.random.nextInt(1, 4))
		);
	}

	public DTOAuthor generateAuthor()
	{
		return new DTOAuthor(
			this.faker.internet().emailAddress(),
			this.faker.name().firstName(),
			this.faker.name().lastName(),
			this.generateAddresses(this.random.nextInt(1, 3))
		);
	}

	public DTOBook generateBook()
	{
		return new DTOBook(
			faker.code().isbn10(true),
			faker.book().title(),
			LocalDate.of(random.nextInt(1990, 2024), random.nextInt(1, 13), random.nextInt(1, 28)),
			random.nextInt(1, 4),
			random.nextInt(100, 5001),
			random.nextDouble(),
			authorsPool.get(random.nextInt(authorsPool.size())),
			publisherPool.get(random.nextInt(publisherPool.size()))
		);

	}

	public List<DTOBook> generateBooks(final int amount)
	{
		return IntStream.range(0, amount).parallel().mapToObj(i -> generateBook()).toList();
	}

	public DTOAddress generateAddress()
	{
		return new DTOAddress(
			this.faker.address().streetName(),
			this.faker.address().streetAddressNumber(),
			this.faker.address().zipCode(),
			this.faker.address().city(),
			this.faker.address().country()
		);
	}

	public List<DTOAddress> generateAddresses(final int amount)
	{
		return IntStream.range(0, amount).mapToObj(i -> this.generateAddress()).toList();
	}

	private <T> List<T> generatePool(int size, Supplier<T> generator)
	{
		return IntStream.range(0, size)
			.parallel()
			.mapToObj(i -> generator.get())
			.collect(() -> new ArrayList<>(size), List::add, List::addAll);
	}

	@WebListener
	public static class StartupDataGenerator implements ServletContextListener
	{
		@Override
		public void contextInitialized(ServletContextEvent event)
		{
			// Only generate when no data is present
			if (Files.exists(MOCKUP_DATA_PATH))
			{
				LOG.info("Mockup Data already generated at {}. Skipping generation.", MOCKUP_DATA_PATH.toAbsolutePath());
				return;
			}

			LOG.info("Generating mockup data at {}", MOCKUP_DATA_PATH.toAbsolutePath());

			try
			{
				final var books = new MockupData(10_000, 1_000).generateBooks(1_000_000);
				final var mapper = new ObjectMapper().findAndRegisterModules().writerWithDefaultPrettyPrinter();
				mapper.writeValue(MOCKUP_DATA_PATH.toFile(), books);
			}
			catch (Exception e)
			{
				LOG.error("Failed to write mockup data file", e);
			}

			LOG.info("Finished generating mockup data");
		}
	}
}
