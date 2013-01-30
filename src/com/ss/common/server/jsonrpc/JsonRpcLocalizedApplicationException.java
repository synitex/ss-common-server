package com.ss.common.server.jsonrpc;

/**
 * Localized version of {@link JsonRpcApplicationException}.
 * To client will be passed error message based on current user language,
 * message key and message prefix.
 * @author sergey.sinica
 *
 */
public class JsonRpcLocalizedApplicationException extends JsonRpcApplicationException {
	private static final long serialVersionUID = 1L;

	private String messageKey;

	private String messagePrefix;

	public JsonRpcLocalizedApplicationException(String id, String messageKey, String messagePrefix) {
		super(id);
		this.messageKey = messageKey;
		this.messagePrefix = messagePrefix;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public String getMessagePrefix() {
		return messagePrefix;
	}

}
