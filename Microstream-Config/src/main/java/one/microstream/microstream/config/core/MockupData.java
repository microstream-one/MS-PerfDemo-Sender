
package one.microstream.microstream.config.core;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.IntStream;

import com.github.javafaker.Faker;

import one.microstream.microstream.config.dto.DTOAddress;
import one.microstream.microstream.config.dto.DTOAuthor;
import one.microstream.microstream.config.dto.DTOBook;
import one.microstream.microstream.config.dto.DTOPublisher;

public class MockupData
{
	private final List<DTOAuthor> authorsPool = new ArrayList<>();
	private final List<DTOPublisher> publisherPool = new ArrayList<>();

	private final Faker faker = new Faker(Locale.US);
	private final Random random = new Random();

	public MockupData()
	{
		fillAuthorsPool(1000);
		fillPublishersPool(100);
	}

	public DTOBook generateBook()
	{
		return new DTOBook(
			this.faker.code().isbn10(true),
			this.faker.book().title(),
			LocalDate.now(),
			this.random.nextInt(1, 3),
			this.random.nextInt(100, 5000),
			this.random.nextDouble(),
			this.randomAuthor(),
			this.randomPublisher()
		);

	}

	public List<DTOBook> generateBooks(final int amount)
	{
		return IntStream.range(0, amount)
			.mapToObj(
				i -> new DTOBook(
					this.faker.code().isbn10(true),
					this.faker.book().title(),
					LocalDate.now(),
					this.random.nextInt(1, 3),
					this.random.nextInt(100, 5000),
					this.random.nextDouble(),
					this.randomAuthor(),
					this.randomPublisher()
				)
			)
			.toList();
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

	public DTOPublisher generatePublisher()
	{
		return new DTOPublisher(
			this.faker.internet().emailAddress(),
			this.faker.company().name(),
			this.generateAddresses(this.random.nextInt(1, 4))
		);
	}

	public List<DTOAddress> generateAddresses(final int amount)
	{
		final ArrayList<DTOAddress> addresses = new ArrayList<>();

		IntStream.range(1, amount).forEach(i ->
		{
			addresses.add(
				new DTOAddress(
					this.faker.address().streetName(),
					this.faker.address().streetAddressNumber(),
					this.faker.address().zipCode(),
					this.faker.address().city(),
					this.faker.address().country()
				)
			);
		});

		return addresses;
	}

	private void fillAuthorsPool(final int amount)
	{
		IntStream.range(0, amount).forEach(i ->
		{
			this.authorsPool.add(this.generateAuthor());
		});
	}

	private void fillPublishersPool(final int amount)
	{
		IntStream.range(0, amount).forEach(i ->
		{
			this.publisherPool.add(this.generatePublisher());
		});
	}

	private DTOAuthor randomAuthor()
	{
		return this.authorsPool.get(this.random.nextInt(0, this.authorsPool.size()));
	}

	private DTOPublisher randomPublisher()
	{
		return this.publisherPool.get(this.random.nextInt(0, this.publisherPool.size()));
	}
}
