package com.ss.common.server.jsonrpc;

/**
 * Generic Json-Rpc services exception, which can be thrown while 
 * parsing service arguments, performing actual call or serializing call results.
 * 
 * This is a runtime exception. If this exception will be thrown during Json-Rpc service call
 * then to client will be passed generic exception dto.
 * @author sergey.sinica
 *
 */
public class JsonRpcCallException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public JsonRpcCallException(String msg) {
		super(msg);
	}

	public JsonRpcCallException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
