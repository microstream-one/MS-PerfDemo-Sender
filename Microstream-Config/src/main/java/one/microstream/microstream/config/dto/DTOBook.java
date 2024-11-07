
package one.microstream.microstream.config.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

public record DTOBook(
	String ISBN,
	String title,
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd") LocalDate publicationDate,
	int edition,
	int availableQuantity,
	double price,
	DTOAuthor author,
	DTOPublisher publisher
)
{
	@Override
	public String toString()
	{
		return title;
	}
}
