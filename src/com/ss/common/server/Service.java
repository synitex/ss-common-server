package com.ss.common.server;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

public interface Service {

	String getServiceId();
	
	boolean process(String method, JsonObject params, HttpServletRequest request, HttpServletResponse response) throws IOException;

}
