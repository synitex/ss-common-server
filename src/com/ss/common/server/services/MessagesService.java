package com.ss.common.server.services;

public interface MessagesService {

	public static final String PREFIX_SERVER = "server";

	public String getMsg(String prefix, String key);
}
