
package one.microstream.microstream.config.dto;

import java.util.List;


public record DTOAuthor(String mail, String firstname, String lastname, List<DTOAddress> addresses)
{

}
