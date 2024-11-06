
package one.microstream.microstream.config.core;

import java.util.Timer;
import java.util.stream.IntStream;

import one.microstream.microstream.config.core.threads.TaskBookCreatePostgreSQL;


public class BookCreateScheduler extends SenderScheduler
{
	public BookCreateScheduler(final int AmountThreads, final long sendDelayMS, final long rampUpDelayMS)
	{
		super(AmountThreads, sendDelayMS, rampUpDelayMS);
	}

	@Override
	public void start()
	{
		IntStream.range(0, this.amountThreads).forEach(timer -> {
			final Timer t = new Timer();
			t.schedule(new TaskBookCreatePostgreSQL(), 0, this.sendDelayMS);
			SenderScheduler.tasks.add(t);

			try
			{
				Thread.sleep(this.rampUpDelayMS);
			}
			catch(final InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
}
