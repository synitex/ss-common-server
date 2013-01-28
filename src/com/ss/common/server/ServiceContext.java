package com.ss.common.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Context, which stores info about current request.
 * @author sergey.sinica
 *
 */
public class ServiceContext {

	private HttpServletRequest req;
	private HttpServletResponse resp;

	public ServiceContext(HttpServletRequest req, HttpServletResponse resp) {
		this.req = req;
		this.resp = resp;
	}

	public HttpServletRequest getRequest() {
		return req;
	}

	public HttpServletResponse getResponse() {
		return resp;
	}

}
