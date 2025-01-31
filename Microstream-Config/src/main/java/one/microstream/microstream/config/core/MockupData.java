
package one.microstream.microstream.config.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import one.microstream.microstream.config.dto.DTOAddress;
import one.microstream.microstream.config.dto.DTOAuthor;
import one.microstream.microstream.config.dto.DTOBook;
import one.microstream.microstream.config.dto.DTOPublisher;

public class MockupData
{
	private static final Logger LOG = LoggerFactory.getLogger(MockupData.class);

	private final List<DTOAuthor> authorsPool;
	private final List<DTOPublisher> publisherPool;

	private final MockupDataGenerator dataGenerator;
	private final Random random = new Random();

	public static List<DTOBook> generateData(int booksCount, int authorPoolSize, int publisherPoolSize)
	{
		LOG.info("Generating mockup data");
		final var data = new MockupData(authorPoolSize, publisherPoolSize).generateBooks(booksCount);
		LOG.info("Finished generating mockup data");
		return data;
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
