package com.ss.common.server.jsonrpc;

import java.util.List;

import com.ss.common.gwt.jsonrpc.shared.DataValidationDto;

public class JsonRpcDataValidationException extends JsonRpcApplicationException {
	private static final long serialVersionUID = 1L;

	private List<DataValidationDto> validations;

	private String prefix;

	public JsonRpcDataValidationException(String prefix, List<DataValidationDto> validations) {
		super("invalid.data");
		this.validations = validations;
		this.prefix = prefix;
	}

	public List<DataValidationDto> getValidations() {
		return validations;
	}

	public String getPrefix() {
		return prefix;
	}

}
