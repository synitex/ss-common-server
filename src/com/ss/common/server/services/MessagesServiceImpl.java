package com.ss.common.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessagesServiceImpl implements MessagesService {

	@Autowired
	private LangService langService;

	public MessagesServiceImpl() {

	}

	@Override
	public String getMsg(String prefix, String key) {
		return "";
	}

}
