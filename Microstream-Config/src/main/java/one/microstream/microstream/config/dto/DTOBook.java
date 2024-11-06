
package one.microstream.microstream.config.dto;

import java.time.LocalDate;


public record DTOBook(
	String ISBN,
	String title,
	LocalDate publicationDate,
	int edition,
	int availableQuantity,
	double price,
	DTOAuthor author,
	DTOPublisher publisher)
{
	
}
