package com.ss.common.server;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang.time.StopWatch;

/**
 * Simple class to calculate elapsed time.
 * Useful utility to meter and log, for example, how long some method performs. 
 * @author sergey.sinica
 *
 */
public class BigBen {

	private StopWatch watch;

	/**
	 * Default constructor. Starts timer immediately.
	 */
	public BigBen() {
		this(true);
	}

	public BigBen(boolean startImmediately) {
		watch = new StopWatch();
		if (startImmediately) {
			start();
		}
	}

	public BigBen start() {
		watch.start();
		return this;
	}

	public BigBen stop() {
		watch.stop();
		return this;
	}

	public long getElapsedTime() {
		return watch.getTime();
	}

	public String getElapsedTimeFormatted() {
		long time = watch.getTime();
		return getElapsedTimeFormatted(time);
	}

	public static String getElapsedTimeFormatted(long periodInMs) {
		if (periodInMs < 10000) {
			return DurationFormatUtils.formatDuration(periodInMs, "S") + "ms";
		} else {
			return DurationFormatUtils.formatDuration(periodInMs, "H:m:s:S");
		}
	}

}
