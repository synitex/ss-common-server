package com.ss.common.server.jsonrpc;

import com.ss.common.gwt.jsonrpc.shared.ErrorDto;

public class ServerErrorDto extends ErrorDto {

	public ServerErrorDto() {
	}

	public ServerErrorDto(String message) {
		super(message);
	}

	@Override
	public String toJson() {
		return ServerJsonHelper.GSON.toJson(this);
	}

	@Override
	public ServerErrorDto fromJson(String data) {
		return ServerJsonHelper.GSON.fromJson(data, ServerErrorDto.class);
	}


}
