
package one.microstream.microstream.config.ui;

import com.rapidclipse.framework.security.authentication.CredentialsUsernamePassword;
import com.rapidclipse.framework.server.security.authentication.Authentication;
import com.rapidclipse.framework.server.security.authentication.CookieBasedAuthenticationMemoizer;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyPressEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import one.microstream.microstream.config.core.auth.MyAuthenticationProvider;

@com.rapidclipse.framework.server.security.authentication.LoginView
@Route("/login")
public class LoginView extends VerticalLayout
{
	public LoginView()
	{
		super();
		this.initUI();
		
		txtUsername.setValue("123");
		txtPassword.setValue("123");
	}

	/**
	 * Event handler delegate method for the {@link Button} {@link #cmdLogin}.
	 *
	 * @see ComponentEventListener#onComponentEvent(ComponentEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void cmdLogin_onClick(final ClickEvent<Button> unused)
	{
		final CredentialsUsernamePassword credentials = CredentialsUsernamePassword.New(
			this.txtUsername.getValue(),
			this.txtPassword.getValue()
		);
		final boolean remember = this.chkRemember.getValue();

		final MyAuthenticationProvider authenticatorProvider = MyAuthenticationProvider.getInstance();

		if (Authentication.tryLogin(credentials, authenticatorProvider))
		{
			if (remember)
			{
				CookieBasedAuthenticationMemoizer.getCurrent().remember(credentials);
			}
		}
		else
		{
			Notification.show("Invalid username/password");
		}
	}

	/**
	 * Event handler delegate method for the {@link Button}
	 * {@link #cmdForgotPassword}.
	 *
	 * @see ComponentEventListener#onComponentEvent(ComponentEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void cmdForgotPassword_onClick(final ClickEvent<Button> event)
	{
		// TODO provide password recovery
	}

	/**
	 * Event handler delegate method for the {@link PasswordField}
	 * {@link #txtPassword}.
	 *
	 * @see ComponentEventListener#onComponentEvent(ComponentEvent)
	 * @eventHandlerDelegate Do NOT delete, used by UI designer!
	 */
	private void txtPassword_onKeyPress(final KeyPressEvent event)
	{
		if (event.getKey().equals(Key.ENTER))
		{
			this.cmdLogin_onClick(null);
		}
	}

	/*
	 * WARNING: Do NOT edit!<br>The content of this method is always regenerated by
	 * the UI designer.
	 */
	// <generated-code name="initUI">
	private void initUI()
	{
		this.verticalLayout = new VerticalLayout();
		this.formLayout = new FormLayout();
		this.h3 = new H3();
		this.txtUsername = new TextField();
		this.txtPassword = new PasswordField();
		this.horizontalLayout = new HorizontalLayout();
		this.chkRemember = new Checkbox();
		this.cmdForgotPassword = new Button();
		this.cmdLogin = new Button();

		this.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
		this.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
		this.h3.setText("Login");
		this.txtUsername.setLabel("Username");
		this.txtPassword.setLabel("Password");
		this.horizontalLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
		this.chkRemember.setLabel("Remember Me");
		this.cmdForgotPassword.setText("Forgot Password?");
		this.cmdForgotPassword.setThemeName("small tertiary");
		this.cmdLogin.setText("Login");

		this.h3.setSizeUndefined();
		this.txtUsername.setSizeUndefined();
		this.txtPassword.setSizeUndefined();
		this.formLayout.add(this.h3, this.txtUsername, this.txtPassword);
		this.chkRemember.setSizeUndefined();
		this.cmdForgotPassword.setSizeUndefined();
		this.cmdLogin.setSizeUndefined();
		this.horizontalLayout.add(this.chkRemember, this.cmdForgotPassword, this.cmdLogin);
		this.horizontalLayout.setFlexGrow(1.0, this.chkRemember);
		this.formLayout.setSizeUndefined();
		this.horizontalLayout.setSizeUndefined();
		this.verticalLayout.add(this.formLayout, this.horizontalLayout);
		this.verticalLayout.setSizeUndefined();
		this.add(this.verticalLayout);
		this.setSizeFull();

		this.txtPassword.addKeyPressListener(this::txtPassword_onKeyPress);
		this.cmdForgotPassword.addClickListener(this::cmdForgotPassword_onClick);
		this.cmdLogin.addClickListener(this::cmdLogin_onClick);
	} // </generated-code>

	// <generated-code name="variables">
	private FormLayout formLayout;
	private Checkbox chkRemember;
	private Button cmdForgotPassword, cmdLogin;
	private VerticalLayout verticalLayout;
	private PasswordField txtPassword;
	private HorizontalLayout horizontalLayout;
	private H3 h3;
	private TextField txtUsername;
	// </generated-code>

}
