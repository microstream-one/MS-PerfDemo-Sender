
package one.microstream.microstream.config.core.auth;

import com.rapidclipse.framework.security.authentication.Authenticator;
import com.rapidclipse.framework.security.authentication.AuthenticatorProvider;
import com.rapidclipse.framework.security.authentication.CredentialsUsernamePassword;
import com.rapidclipse.framework.security.util.PasswordHasher;

import one.microstream.microstream.config.domain.User;


public class MyAuthenticationProvider
	implements AuthenticatorProvider<CredentialsUsernamePassword, CredentialsUsernamePassword>
{
	private static class InitializationOnDemandHolder
	{
		final static MyAuthenticationProvider INSTANCE = new MyAuthenticationProvider();
	}

	public static MyAuthenticationProvider getInstance()
	{
		return InitializationOnDemandHolder.INSTANCE;
	}

	private final PasswordHasher     hashStrategy = PasswordHasher.Sha2();
	private MicroStreamAuthenticator authenticator;

	private MyAuthenticationProvider()
	{
	}

	@Override
	public Authenticator<CredentialsUsernamePassword, CredentialsUsernamePassword> provideAuthenticator()
	{
		if(this.authenticator == null)
		{
			this.authenticator = new MicroStreamAuthenticator(User.class);
			this.authenticator.setHashStrategy(this.getHashStrategy());
		}

		return this.authenticator;
	}

	public PasswordHasher getHashStrategy()
	{
		return this.hashStrategy;
	}
}
