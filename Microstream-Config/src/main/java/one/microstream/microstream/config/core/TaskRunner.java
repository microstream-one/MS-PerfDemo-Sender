
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
	private final int runCount;
	private final int delayMs;
	private final Supplier<Runnable> taskGenerator;
	private final Runnable onTasksFinished;

	public TaskRunner(
		final int threadCount,
		final int runCount,
		final int delayMs,
		final Supplier<Runnable> taskGenerator,
		final Runnable onTasksFinished
	)
	{
		this.executor = Executors.newFixedThreadPool(threadCount);
		this.runCount = runCount;
		this.delayMs = delayMs;
		this.taskGenerator = taskGenerator;
		this.onTasksFinished = onTasksFinished;
	}

	public void start()
	{
		final var finishCount = new AtomicInteger(0);

		for (int i = 0; i < this.runCount; i++)
		{
			final int number = i;
			final Runnable task = this.taskGenerator.get();
			this.executor.execute(() ->
			{
				LOG.trace("Executing run {}", number);

				try
				{
					task.run();
					if (this.delayMs > 0)
					{
						ThreadUtils.trySleep(this.delayMs);
					}
				}
				catch (final Exception e)
				{
					LOG.error("Run {} failed executing task", number, e);
				}

				LOG.trace("Run {} done", number);
				if (finishCount.incrementAndGet() == this.runCount)
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
