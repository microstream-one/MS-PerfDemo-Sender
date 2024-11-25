
package one.microstream.microstream.config.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({
	"id", "UUID"
})
public record DTOPublisher(String mail, String company, List<DTOAddress> addresses)
{
}
