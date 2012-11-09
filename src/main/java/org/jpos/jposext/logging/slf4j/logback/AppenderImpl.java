package org.jpos.jposext.logging.slf4j.logback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jpos.util.Log;
import org.jpos.util.LogEvent;
import org.jpos.util.Loggeable;
import org.jpos.util.Logger;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggingEventVO;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.LogbackException;
import ch.qos.logback.core.encoder.Encoder;

/**
 * This work is inspired by initial Victor Salaman logback integration work on <a href="https://github.com/jpos/jPOS-EE">jPOS-EE</a> which is the reference logback implementation for jPOS-EE. 
 * <p>
 * This logback appender implementation deals with Loggeable args in a way so that their formatting is delegated to the underlying jPos logger.<br>
 * Support for logback Encoder is also added.<br>
 * </p>
 * @author dgrandemange
 */
public class AppenderImpl extends AppenderBase<ILoggingEvent> {

	protected static final String DEFAULT_ENCODER_PATTERN = "%msg%n";

	protected static final String LOGGEABLE_FLAG = "@LOGGEABLE_TOKEN@";

	private static final Pattern LOGGEABLE_REGEXPPATTERN = Pattern
			.compile("@LOGGEABLE_TOKEN@");

	private Logger logger;

	private Encoder<ILoggingEvent> encoder;

	@Override
	public void start() {
		this.logger = Logger.getLogger(getName());

		if (this.encoder == null) {
			initDefaultEncoder();
		}

		super.start();
	}

	@Override
	protected void append(final ILoggingEvent event) {
		if (!isStarted()) {
			return;
		}

		// Build
		final Log source = new Log(logger, getName() + "/"
				+ event.getLoggerName());

		final LogEvent jposLogEvent = new LogEvent(source,
				event.getLevel().levelStr.toLowerCase());

		// Populate jPos log event from logback event
		populateJPosLogEvent(event, jposLogEvent);

		// Do the actual logging
		Logger.log(jposLogEvent);
	}

	protected void handleThrowableProxy(final LogEvent jposLogEvent,
			IThrowableProxy tp) {
		boolean handled = false;
		if (tp != null) {
			if (tp instanceof ThrowableProxy) {
				Throwable t = ((ThrowableProxy) tp).getThrowable();
				if (t instanceof Loggeable) {
					jposLogEvent.addMessage(t);
					handled = true;
				}
			}
		}
		
		if (!handled) {
			jposLogEvent.addMessage(ThrowableProxyUtil.asString(tp));
		}
	}

	protected void initDefaultEncoder() {
		PatternLayoutEncoder pl = new PatternLayoutEncoder();
		pl.setContext(context);
		pl.setPattern(DEFAULT_ENCODER_PATTERN);
		pl.start();
		this.encoder = pl;
	}

	/**
	 * Populate the jPos log event from the incoming Logback event <br>
	 * 
	 * Special care is made to the logback event parameters to preserve any jpos
	 * Loggeable objects from being formated by Logback. <br>
	 * Doing so, we delegate the handling of any Loggeable instances to the
	 * underlying jPos logger which knows very well how to deal with such
	 * loggeables, especially how to properly format them by calling their
	 * Loggeable.dump(...) method.<br>
	 * 
	 * @param event
	 *            Incoming logback event
	 * @param ev
	 *            The jPos log event to populate messages with
	 */
	protected void populateJPosLogEvent(ILoggingEvent originalEvent, LogEvent ev) {
		// We must work with a copy of the original logback event, as once the
		// formatted message is computed on an event, it is not refreshable
		ILoggingEvent event = LoggingEventVO.build(originalEvent);

		List<Loggeable> lstLoggeable = null;
		Object[] evArgArray = event.getArgumentArray();
		if (null != evArgArray) {
			for (int i = 0; i < evArgArray.length; i++) {
				Object argument = evArgArray[i];
				if (argument instanceof Loggeable) {
					if (null == lstLoggeable) {
						lstLoggeable = new ArrayList<Loggeable>();
					}
					lstLoggeable.add((Loggeable) argument);
					evArgArray[i] = LOGGEABLE_FLAG;
				}
			}
		}

		String formattedMessage = getEncoded(event);

		if (null == lstLoggeable) {
			ev.addMessage(formattedMessage);
		} else {
			int previousMatchEndPos = 0;
			int idx = 0;

			Matcher match = LOGGEABLE_REGEXPPATTERN.matcher(formattedMessage);

			while (match.find()) {
				int beginIndex = previousMatchEndPos;
				int endIndex = match.start();

				if (endIndex > 0) {
					String nonLoggeablePart = formattedMessage.substring(
							beginIndex, endIndex);
					if (nonLoggeablePart.length() > 0) {
						ev.addMessage(nonLoggeablePart);
					}
				}

				ev.addMessage(lstLoggeable.get(idx));

				previousMatchEndPos = match.end();
				idx++;
			}

			if (previousMatchEndPos < formattedMessage.length()) {
				ev.addMessage(formattedMessage.substring(previousMatchEndPos));
			}
		}
	}

	/**
	 * @param event
	 *            Incoming logback event
	 * @return Encoded event using the current instance encoder
	 */
	protected String getEncoded(ILoggingEvent event) {
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			encoder.init(bos);
			this.encoder.doEncode(event);
			return new String(bos.toByteArray());
		} catch (IOException e) {
			throw new LogbackException(e.getMessage(), e);
		} finally {
			if (null != bos) {
				try {
					bos.close();
				} catch (IOException e) {
					throw new LogbackException(e.getMessage(), e);
				}
			}
		}
	}

	public Encoder<ILoggingEvent> getEncoder() {
		return encoder;
	}

	public void setEncoder(Encoder<ILoggingEvent> encoder) {
		this.encoder = encoder;
	}
}
