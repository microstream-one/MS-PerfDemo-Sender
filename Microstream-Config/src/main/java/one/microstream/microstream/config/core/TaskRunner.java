
package one.microstream.microstream.config.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import one.microstream.microstream.config.core.utils.ThreadUtils;

public class TaskRunner
{
	private static final Logger LOG = LoggerFactory.getLogger(TaskRunner.class);

	private final ExecutorService executor;
	private final HttpTask task;
	private final BookstoreHttpClient httpClient;
	private final int threadCount;
	private final int threadRunCount;
	private final int delayMs;
	private final Runnable onTasksFinished;

	private volatile boolean isRunning = true;

	public TaskRunner(
		final int threadCount,
		final int threadRunCount,
		final int delayMs,
		final HttpTask task,
		final BookstoreHttpClient httpClient,
		final Runnable onTasksFinished
	)
	{
		this.executor = Executors.newFixedThreadPool(threadCount);
		this.httpClient = httpClient;
		this.threadCount = threadCount;
		this.threadRunCount = threadRunCount;
		this.delayMs = delayMs;
		this.task = task;
		this.onTasksFinished = onTasksFinished;
	}

	public void start()
	{
		final var finishCount = new AtomicInteger(0);

		for (int i = 0; i < this.threadCount; i++)
		{
			final int number = i;
			this.executor.execute(() ->
			{
				LOG.info("Starting thread {}", number);

				try
				{
					for (int j = 0; j < this.threadRunCount && this.isRunning; j++)
					{
						this.task.run(httpClient);
						if (this.delayMs > 0)
						{
							ThreadUtils.trySleep(this.delayMs);
						}
					}
				}
				catch (final Exception e)
				{
					LOG.error("Thread {} failed executing task", number, e);
				}

				LOG.info("Thread {} done", number);
				if (finishCount.incrementAndGet() == this.threadCount)
				{
					LOG.info("All threads are done. Telling the ui...");
					this.onTasksFinished.run();
				}
			});
		}
	}

	public void stop()
	{
		this.executor.shutdown();
	}

	public void waitForTasks()
	{
		try
		{
			this.executor.awaitTermination(10, TimeUnit.SECONDS);
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}
}
