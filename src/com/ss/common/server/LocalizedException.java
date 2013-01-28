package com.ss.common.server;

/**
 * Business exception. Service can throw this exception at any time to pass to
 * client localized error message.
 * 
 * @author sergey.sinica
 * 
 */
public class LocalizedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private String errorKey;

	public LocalizedException(String errorKey) {
		this.errorKey = errorKey;
	}

	public LocalizedException(String errorKey, Throwable cause) {
		super(cause);
		this.errorKey = errorKey;
	}

	public LocalizedException(String errorKey, String cause) {
		super(cause);
		this.errorKey = errorKey;
	}
	
	public LocalizedException(String errorKey, String cause, Throwable causeException) {
		super(cause, causeException);
		this.errorKey = errorKey;
	}

	public String getErrorKey() {
		return errorKey;
	}

}
