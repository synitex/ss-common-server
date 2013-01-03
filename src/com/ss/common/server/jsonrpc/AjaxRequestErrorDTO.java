package com.ss.common.server.jsonrpc;

import com.google.gson.annotations.SerializedName;
import com.ss.common.gwt.jsonrpc.shared.AjaxRequestConstants;

public class AjaxRequestErrorDTO {

	@SerializedName(AjaxRequestConstants.ERROR)
	private String errorKey;

	public AjaxRequestErrorDTO(String errorKey) {
		this.errorKey = errorKey;
	}

	public String getErrorKey() {
		return errorKey;
	}

	public void setErrorKey(String errorKey) {
		this.errorKey = errorKey;
	}

}
