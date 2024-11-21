
package one.microstream.microstream.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidclipse.framework.server.RapServletService;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinSession;

public class LogExtension implements RapServletService.Extension
{
	private static final Logger LOG = LoggerFactory.getLogger(LogExtension.class);

	@Override
	public void sessionCreated(RapServletService service, VaadinSession session, VaadinRequest request)
	{
		session.setErrorHandler(
			event -> LOG.error(
				"Servlet Exception. Component: {}, Element: {}",
				event.getComponent(),
				event.getElement(),
				event.getThrowable()
			)
		);
	}
}
