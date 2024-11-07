
package one.microstream.microstream.config;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;


@PWA(name = "", shortName = "")
@Push
public class AppShell implements AppShellConfigurator
{
	@Override
	public void configurePage(AppShellSettings settings)
	{
		settings.addFavIcon("icon", "frontend/images/favicon256.png", "256x256");
		settings.addLink("shortcut icon", "frontend/images/favicon.ico");
	}
}
