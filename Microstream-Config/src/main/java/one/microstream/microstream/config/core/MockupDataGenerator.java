package one.microstream.microstream.config.core;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.IntStream;

import com.github.javafaker.Faker;

import one.microstream.microstream.config.dto.DTOAddress;
import one.microstream.microstream.config.dto.DTOAuthor;
import one.microstream.microstream.config.dto.DTOBook;
import one.microstream.microstream.config.dto.DTOPublisher;

public class MockupDataGenerator
{
	private final Random random;
	private final Faker faker;

	public MockupDataGenerator(long rngSeed)
	{
		this(new Random(rngSeed));
	}

	public MockupDataGenerator(Random random)
	{
		this.random = random;
		faker = new Faker(Locale.US, random);
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
		return generateBook(generateAuthor(), generatePublisher());
	}

	public DTOBook generateBook(DTOAuthor author, DTOPublisher publisher)
	{
		return new DTOBook(
			faker.code().isbn10(true),
			faker.book().title(),
			LocalDate.of(random.nextInt(1990, 2024), random.nextInt(1, 13), random.nextInt(1, 28)),
			random.nextInt(1, 4),
			random.nextInt(100, 5001),
			random.nextDouble(),
			author,
			publisher
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
}
