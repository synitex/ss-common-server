package com.ss.common.server.jsonrpc;

/**
 * This is a super type for all Json-Rpc services application (business) exceptions.
 * Usually such kind of exceptions are defined as checked exceptions and you 
 * throw them in the code, to indicate some business exception. 
 * <br/><br/>
 * For example, WrongPasswordException is a good candidate to be business exception.
 * <br/><br/>
 * Each {@link JsonRpcApplicationException} is described with some id, which will
 * be passed to client as error dto, and client, based on this id, can perform some action.
 * For example, shows warning dialog "Wrong password" and points to password input field.
 * @author sergey.sinica
 *
 */
public abstract class JsonRpcApplicationException extends Exception {
	private static final long serialVersionUID = 1L;

	private String id;

	public JsonRpcApplicationException(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

}
