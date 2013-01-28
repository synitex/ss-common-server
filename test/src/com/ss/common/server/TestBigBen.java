package com.ss.common.server;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestBigBen {

	private static final Logger log = LoggerFactory.getLogger(TestBigBen.class);

	@Test
	public void testBigBen() {
		testImpl(TimeUnit.MILLISECONDS.toMillis(2));
		testImpl(TimeUnit.MILLISECONDS.toMillis(23));
		testImpl(TimeUnit.MILLISECONDS.toMillis(745));
		testImpl(TimeUnit.MILLISECONDS.toMillis(1000));
		testImpl(TimeUnit.MILLISECONDS.toMillis(5060));
		testImpl(TimeUnit.MILLISECONDS.toMillis(50060));
		testImpl(TimeUnit.MILLISECONDS.toMillis(505060));
		testImpl(TimeUnit.MILLISECONDS.toMillis(5050600));
		testImpl(TimeUnit.MILLISECONDS.toMillis(50506008));
		testImpl(TimeUnit.MILLISECONDS.toMillis(505060082));
		testImpl(TimeUnit.MILLISECONDS.toMillis(5050600820L));
	}

	private void testImpl(long period) {
		log.info(period + " => " + BigBen.getElapsedTimeFormatted(period));
	}

}
