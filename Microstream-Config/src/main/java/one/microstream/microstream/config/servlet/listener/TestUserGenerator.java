
package one.microstream.microstream.config.servlet.listener;

import com.rapidclipse.framework.security.util.PasswordHasher;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import one.microstream.microstream.config.dal.UserDAO;
import one.microstream.microstream.config.domain.User;

@WebListener
public class TestUserGenerator implements ServletContextListener
{
	@Override
	public void contextInitialized(final ServletContextEvent sce)
	{
		// FIXME: Test user
		final User user = new User();
		user.setUsername("123");
		user.setPassword(PasswordHasher.Sha2().hashPassword(new String("123").getBytes()));
		UserDAO.addUser(user);
	}
}
