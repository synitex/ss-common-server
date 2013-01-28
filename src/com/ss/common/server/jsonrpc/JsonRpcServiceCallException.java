package com.ss.common.server.jsonrpc;

public class JsonRpcServiceCallException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public JsonRpcServiceCallException(String msg) {
		super(msg);
	}

	public JsonRpcServiceCallException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
