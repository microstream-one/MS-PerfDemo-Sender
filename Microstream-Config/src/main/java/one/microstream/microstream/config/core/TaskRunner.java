
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
	private final int delayMs;
	private final Supplier<Executable> taskSupplier;
	private final Runnable onTasksFinished;

	public TaskRunner(
		final int threadCount,
		final long runCount,
		final int delayMs,
		final Supplier<Executable> taskSupplier,
		final Runnable onTasksFinished
	)
	{
		this.executor = Executors.newFixedThreadPool(threadCount);
		this.threadCount = threadCount;
		this.runCount = runCount;
		this.delayMs = delayMs;
		this.taskSupplier = taskSupplier;
		this.onTasksFinished = onTasksFinished;
	}

	public void start()
	{
		final var finishCount = new AtomicInteger(0);
		System.out.println("Run count: " + runCount);

		for (int i = 0; i < threadCount; i++)
		{
			final var task = taskSupplier.get();
			final int number = i;

			this.executor.execute(() ->
			{
				LOG.trace("Executing thread {}", number);

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
						if (this.delayMs > 0)
						{
							ThreadUtils.trySleep(this.delayMs);
						}

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
