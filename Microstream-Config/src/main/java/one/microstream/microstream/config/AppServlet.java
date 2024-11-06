
package one.microstream.microstream.config;

import com.rapidclipse.framework.server.RapServlet;

import jakarta.servlet.annotation.WebServlet;


@WebServlet(urlPatterns = "/*", asyncSupported = true)
public class AppServlet extends RapServlet
{
	public AppServlet()
	{
		super();
	}
}
