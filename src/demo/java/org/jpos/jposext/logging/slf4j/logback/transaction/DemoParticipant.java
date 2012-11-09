package org.jpos.jposext.logging.slf4j.logback.transaction;

import java.io.PrintStream;
import java.io.Serializable;

import org.jpos.transaction.TransactionConstants;
import org.jpos.transaction.TransactionParticipant;
import org.jpos.util.Loggeable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * @author dgrandemange
 * 
 */
public class DemoParticipant implements TransactionParticipant,
		TransactionConstants {

	private static Logger logger = LoggerFactory
			.getLogger(DemoParticipant.class);

	public int prepare(long id, Serializable context) {
		RuntimeException runtimeException = new RuntimeException(
				"Hey, i am a runtime exception !");
		Loggeable loggeable = new Loggeable() {
			public void dump(PrintStream p, String indent) {
				p.println("This line is formatted by Loggeable.dump()");
			}
		};

		MDC.put("jposTransactionId", ""+id);
		
		logger.info("DEMO1.some simple string");

		logger.info("DEMO2.some '{}' string parameter", "hey  i am a parameter!");

		logger.info("DEMO3.something with a jPos loggeable >> {} << within",
				new Loggeable() {
					public void dump(PrintStream p, String indent) {
						p.println("This line is formatted by Loggeable.dump()");
					}
				});

		logger.error("DEMO4.something with a throwable as parameter : ",
				runtimeException);

		logger.error(
				"DEMO5.something with a loggeable >> {} << plus an exception >> {} << within",
				loggeable, runtimeException);

		return PREPARED | NO_JOIN;
	}

	public void commit(long id, Serializable context) {
	}

	public void abort(long id, Serializable context) {
	}

}
