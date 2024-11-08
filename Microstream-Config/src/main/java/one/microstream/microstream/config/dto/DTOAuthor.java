
package one.microstream.microstream.config.dto;

import java.util.List;

import javax.annotation.Nullable;

public record DTOAuthor(String mail, String firstname, String lastname, @Nullable List<DTOAddress> addresses)
{

}
