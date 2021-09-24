
package one.microstream.microstream.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidclipse.framework.security.util.PasswordHasher;

import one.microstream.microstream.config.dal.UserDAO;
import one.microstream.microstream.config.domain.User;


@WebListener
public class AppContextListener implements ServletContextListener
{

	private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);
	
	@Override
	public void contextInitialized(final ServletContextEvent sce)
	{
		final User user = new User();
		user.setUsername("microstream@local");
		user.setPassword(PasswordHasher.Sha2().hashPassword(new String("haselnuss@08").getBytes()));
		UserDAO.addUser(user);
	}

	@Override
	public void contextDestroyed(final ServletContextEvent sce)
	{
		
	}
}
