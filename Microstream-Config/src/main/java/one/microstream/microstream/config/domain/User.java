
package one.microstream.microstream.config.domain;

import java.io.Serializable;

import com.rapidclipse.framework.security.authentication.CredentialsUsernamePassword;


public class User implements Serializable, CredentialsUsernamePassword
{
	
	private byte[] password;
	private String username;

	public User()
	{
		super();
	}

	public String getUsername()
	{
		return this.username;
	}

	public void setUsername(final String username)
	{
		this.username = username;
	}

	public byte[] getPassword()
	{
		return this.password;
	}

	public void setPassword(final byte[] password)
	{
		this.password = password;
	}

	@Override
	public String username()
	{
		return this.getUsername();
	}

	@Override
	public byte[] password()
	{
		return this.getPassword();
	}
}
