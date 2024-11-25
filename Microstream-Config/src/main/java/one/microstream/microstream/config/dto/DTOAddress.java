
package one.microstream.microstream.config.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({
	"id", "UUID"
})
public record DTOAddress(String address, String address2, String zip, String city, String country)
{
}
