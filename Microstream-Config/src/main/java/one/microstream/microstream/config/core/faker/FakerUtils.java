
package one.microstream.microstream.config.core.faker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Random;
import java.util.stream.IntStream;

import com.github.javafaker.Faker;

import one.microstream.microstream.config.dto.DTOAddress;
import one.microstream.microstream.config.dto.DTOAuthor;
import one.microstream.microstream.config.dto.DTOBook;
import one.microstream.microstream.config.dto.DTOPublisher;


public class FakerUtils
{
	private static final List<DTOAuthor>    authorsPool   = new ArrayList<>();
	private static final List<DTOPublisher> publisherPool = new ArrayList<>();
	
	private static final Faker  faker  = new Faker(Locale.US);
	private static final Random random = new Random();
	
	static
	{
		FakerUtils.fillAuthorsPool(1000);
		FakerUtils.fillPublishersPool(100);
	}
	
	public static DTOBook generateBook()
	{
		return new DTOBook(FakerUtils.faker.code().isbn10(true), FakerUtils.faker.book().title(), LocalDate.now(),
			FakerUtils.random.nextInt(1, 3), FakerUtils.random.nextInt(100, 5000), FakerUtils.random.nextDouble(),
			FakerUtils.randomAuthor(),
			FakerUtils.randomPublisher());
		
	}
	
	public static Queue<DTOBook> generateBooks(final int amount)
	{
		final Queue<DTOBook> booksQueue = new LinkedList<>();
		
		IntStream.range(1, amount).forEach(i -> {
			
			booksQueue.add(new DTOBook(FakerUtils.faker.code().isbn10(true), FakerUtils.faker.book().title(),
				LocalDate.now(),
				FakerUtils.random.nextInt(1, 3), FakerUtils.random.nextInt(100, 5000), FakerUtils.random.nextDouble(),
				FakerUtils.randomAuthor(),
				FakerUtils.randomPublisher()));
		});
		
		return booksQueue;
	}
	
	public static DTOAuthor generateAuthor()
	{
		return new DTOAuthor(FakerUtils.faker.internet().emailAddress(), FakerUtils.faker.name().firstName(),
			FakerUtils.faker.name().lastName(),
			FakerUtils.generateAddresses(FakerUtils.random.nextInt(2, 3)));
	}
	
	public static DTOPublisher generatePublisher()
	{
		return new DTOPublisher(FakerUtils.faker.internet().emailAddress(), FakerUtils.faker.company().name(),
			FakerUtils.generateAddresses(FakerUtils.random.nextInt(2, 4)));
	}
	
	public static List<DTOAddress> generateAddresses(final int amount)
	{
		final ArrayList<DTOAddress> addresses = new ArrayList<>();
		
		IntStream.range(1, amount).forEach(i -> {
			addresses.add(new DTOAddress(FakerUtils.faker.address().streetName(),
				FakerUtils.faker.address().streetAddressNumber(),
				FakerUtils.faker.address().zipCode(), FakerUtils.faker.address().city(),
				FakerUtils.faker.address().country()));
		});

		return addresses;
	}
	
	private static void fillAuthorsPool(final int amount)
	{
		IntStream.range(1, amount).forEach(i -> {
			FakerUtils.authorsPool.add(FakerUtils.generateAuthor());
		});
	}
	
	private static void fillPublishersPool(final int amount)
	{
		IntStream.range(1, amount).forEach(i -> {
			FakerUtils.publisherPool.add(FakerUtils.generatePublisher());
		});
	}
	
	private static DTOAuthor randomAuthor()
	{
		return FakerUtils.authorsPool.get(FakerUtils.random.nextInt(1, FakerUtils.authorsPool.size() - 1));
	}
	
	private static DTOPublisher randomPublisher()
	{
		return FakerUtils.publisherPool.get(FakerUtils.random.nextInt(1, FakerUtils.publisherPool.size() - 1));
	}
}
