package one.microstream.microstream.config.core;

@FunctionalInterface
public interface HttpTask
{
	void run(BookstoreHttpClient client);
}
