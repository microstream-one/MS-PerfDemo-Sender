
package one.microstream.microstream.config.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import one.microstream.microstream.config.dto.DTOAddress;
import one.microstream.microstream.config.dto.DTOAuthor;
import one.microstream.microstream.config.dto.DTOBook;
import one.microstream.microstream.config.dto.DTOPublisher;

public class MockupData
{
	public static final Path MOCKUP_DATA_PATH = Paths.get(
		Optional.ofNullable(System.getenv("PERF_DEMO_DATA_PATH")).orElse("/storage/ms-perf-demo-data.json")
	);

	private static final Logger LOG = LoggerFactory.getLogger(MockupData.class);

	private final List<DTOAuthor> authorsPool;
	private final List<DTOPublisher> publisherPool;

	private final MockupDataGenerator dataGenerator;
	private final Random random = new Random();

	public static void generateData(int booksCount, int authorPoolSize, int publisherPoolSize) throws IOException
	{
		// Only generate when no data is present
		if (Files.exists(MOCKUP_DATA_PATH))
		{
			LOG.info("Mockup Data already generated at {}. Removing file.", MOCKUP_DATA_PATH.toAbsolutePath());
			Files.delete(MOCKUP_DATA_PATH);
		}

		LOG.info("Generating mockup data at {}", MOCKUP_DATA_PATH.toAbsolutePath());

		final var books = new MockupData(authorPoolSize, publisherPoolSize).generateBooks(booksCount);
		final var mapper = new ObjectMapper().findAndRegisterModules().writerWithDefaultPrettyPrinter();
		mapper.writeValue(MOCKUP_DATA_PATH.toFile(), books);

		LOG.info("Finished generating mockup data");
	}

	public static List<DTOBook> loadData() throws IOException
	{
		final var mapper = new ObjectMapper().findAndRegisterModules();
		return mapper.readValue(MOCKUP_DATA_PATH.toFile(), new TypeReference<List<DTOBook>>()
		{
		});
	}

	private MockupData(int authorPoolSize, int publisherPoolSize)
	{
		this.dataGenerator = new MockupDataGenerator(random);
		this.authorsPool = generatePool(authorPoolSize, this::generateAuthor);
		this.publisherPool = generatePool(publisherPoolSize, this::generatePublisher);
	}

	public DTOPublisher generatePublisher()
	{
		return this.dataGenerator.generatePublisher();
	}

	public DTOAuthor generateAuthor()
	{
		return this.dataGenerator.generateAuthor();
	}

	public DTOBook generateBook()
	{
		return dataGenerator.generateBook(
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
		return dataGenerator.generateAddress();
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
}
