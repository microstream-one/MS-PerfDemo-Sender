
package one.microstream.microstream.config.dto;

import java.util.List;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({
	"id", "UUID"
})
public record DTOAuthor(String mail, String firstname, String lastname, @Nullable List<DTOAddress> addresses)
{
}
