
package one.microstream.microstream.config.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import one.microstream.microstream.config.core.utils.ThreadUtils;

public class TaskRunner
{
	private static final Logger LOG = LoggerFactory.getLogger(TaskRunner.class);

	private final ExecutorService executor;
	private final long runCount;
	private final int threadCount;
	private final int rampUpDelaySeconds;
	private final Supplier<Executable> taskSupplier;
	private final Runnable onTasksFinished;

	public TaskRunner(
		final int threadCount,
		final long runCount,
		final int rampUpDelaySeconds,
		final Supplier<Executable> taskSupplier,
		final Runnable onTasksFinished
	)
	{
		this.executor = Executors.newFixedThreadPool(threadCount);
		this.threadCount = threadCount;
		this.runCount = runCount;
		this.rampUpDelaySeconds = rampUpDelaySeconds;
		this.taskSupplier = taskSupplier;
		this.onTasksFinished = onTasksFinished;
	}

	public void start()
	{
		final var finishCount = new AtomicInteger(0);

		for (int i = 0; i < threadCount; i++)
		{
			if (rampUpDelaySeconds > 0)
			{
				// Not perfect but good enough for our case of waiting for ramp up
				long rampUpDelayMs = rampUpDelaySeconds * 1000L;
				ThreadUtils.trySleep(rampUpDelayMs / threadCount);
			}

			final var task = taskSupplier.get();
			final int number = i;

			this.executor.execute(() ->
			{
				LOG.info("Running thread {}", number);

				try
				{
					for (long j = 0; j < this.runCount; j++)
					{
						if (Thread.interrupted())
						{
							LOG.info("Task {} was interrupted", number);
							break;
						}

						task.execute();

						LOG.trace("Task {} done", number);
					}
				}
				catch (final Exception e)
				{
					LOG.error("Task {} failed executing task", number, e);
				}

				if (finishCount.incrementAndGet() == this.threadCount)
				{
					LOG.info("All task runs completed");
					this.onTasksFinished.run();
				}
			});
		}

		LOG.info("Started all task threads");
	}

	public void stop()
	{
		final int skipped = this.executor.shutdownNow().size();
		if (skipped > 0)
		{
			LOG.info("Skipped execution of {} tasks", skipped);
		}
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
