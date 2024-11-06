
package one.microstream.microstream.config.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;

import one.microstream.microstream.config.core.threads.TaskBookCreatePostgreSQL;


public class SenderScheduler
{
	private static final List<Timer> tasks = new ArrayList<>();

	private int  amountThreads = 1;
	private long sendDelayMS   = 0L;
	private long rampUpDelayMS = 0L;

	private TimerTask task;

	public SenderScheduler(
		final int AmountThreads,
		final long sendDelayMS,
		final long rampUpDelayMS)
	{
		super();

		this.amountThreads = AmountThreads;
		this.sendDelayMS   = sendDelayMS;
		this.rampUpDelayMS = rampUpDelayMS;

	}

	public void start()
	{
		IntStream.range(0, this.amountThreads).forEach(timer -> {
			final Timer t = new Timer();
			t.schedule(new TaskBookCreatePostgreSQL(), 0, 1);
			SenderScheduler.tasks.add(t);

			try
			{
				System.out.println("Thread erstellt ich warte");
				Thread.sleep(this.rampUpDelayMS);
			}
			catch(final InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	public void stop()
	{
		SenderScheduler.tasks.forEach(t -> {
			t.cancel();
			t.purge();
		});

		SenderScheduler.tasks.clear();
	}

	public void setTask(final TimerTask task)
	{
		this.task = task;
	}
}
