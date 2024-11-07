package one.microstream.microstream.config.core.utils;

public interface ThreadUtils
{
	static void trySleep(final long millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch (final InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}
}
