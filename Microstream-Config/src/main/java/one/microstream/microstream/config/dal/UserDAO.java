
package one.microstream.microstream.config.dal;

import java.util.ArrayList;
import java.util.List;

import one.microstream.microstream.config.domain.User;


public class UserDAO
{
	private static List<User> users = new ArrayList<>();
	
	public static List<User> findAll()
	{

		return UserDAO.users;
	}

	public static void addUser(final User user)
	{
		UserDAO.users.add(user);
	}

}
