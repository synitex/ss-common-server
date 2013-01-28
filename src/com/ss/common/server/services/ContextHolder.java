package com.ss.common.server.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.ss.common.server.ServiceContext;

@Component
public class ContextHolder {

	protected transient ThreadLocal<ServiceContext> perThreadContext;

	public ContextHolder() {

	}

	public void init(HttpServletRequest req, HttpServletResponse resp) {
		synchronized (this) {
			validateThreadLocalData();
			perThreadContext.set(new ServiceContext(req, resp));
		}
	}

	public void clear() {
		perThreadContext.set(null);
	}

	public ServiceContext getContext() {
		synchronized (this) {
			validateThreadLocalData();
			return perThreadContext.get();
		}
	}

	private void validateThreadLocalData() {
		if (perThreadContext == null) {
			perThreadContext = new ThreadLocal<ServiceContext>();
		}
	}
}
