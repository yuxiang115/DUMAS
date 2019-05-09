package util;

import java.util.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
public class StandardLoggingHandler extends Handler
{
	private SimpleFormatter formatter = new SimpleFormatter();

	public void close()
			throws SecurityException
	{}

	public void flush()
	{
		System.out.flush();
	}

	public void publish(LogRecord log)
	{
		System.out.print(getLogMessage(log));
	}

	protected String getLogMessage(LogRecord log)
	{
		return this.formatter.format(log);
	}
}