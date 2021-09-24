
package one.microstream.microstream.config.core;

import java.util.Optional;

import com.rapidclipse.framework.security.authentication.AuthenticationFailedException;
import com.rapidclipse.framework.security.authentication.Authenticator;
import com.rapidclipse.framework.security.authentication.CredentialsUsernamePassword;
import com.rapidclipse.framework.security.util.PasswordHasher;

import one.microstream.microstream.config.dal.UserDAO;
import one.microstream.microstream.config.domain.User;


public class MicroStreamAuthenticator
	implements Authenticator<CredentialsUsernamePassword, CredentialsUsernamePassword>
{
	PasswordHasher hashStrategy;
	
	/**
	 *
	 */
	public MicroStreamAuthenticator(
		final Class<? extends CredentialsUsernamePassword> authenticationEntityType)
	{
	}
	
	public final CredentialsUsernamePassword authenticate(
		final String username,
		final String password)
		throws AuthenticationFailedException
	{
		return this.authenticate(CredentialsUsernamePassword.New(username, password.getBytes()));
	}
	
	@Override
	public CredentialsUsernamePassword authenticate(final CredentialsUsernamePassword credentials)
		throws AuthenticationFailedException
	{
		return this.checkCredentials(credentials);
	}
	
	protected CredentialsUsernamePassword checkCredentials(
		final CredentialsUsernamePassword credentials)
		throws AuthenticationFailedException
	{
		
		final Optional<User> findFirst =
			UserDAO.findAll().stream().filter(
				u -> u.getUsername().equals(credentials.username()) &&
					this.hashStrategy.validatePassword(credentials.password(), u.getPassword()))
				.findFirst();
		
		if(findFirst.isPresent())
		{
			final CredentialsUsernamePassword user = findFirst.get();
			
			return user;
		}
		
		throw new AuthenticationFailedException();
	}
	
	public PasswordHasher getHashStrategy()
	{
		return this.hashStrategy;
	}
	
	public void setHashStrategy(final PasswordHasher hashStrategy)
	{
		this.hashStrategy = hashStrategy;
	}
}
