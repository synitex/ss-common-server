package com.ss.common.server.jsonrpc;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.gson.annotations.SerializedName;
import com.ss.common.gwt.jsonrpc.shared.JsonDto;

public class DemoJsonDto implements JsonDto {

	@SerializedName("p1")
	private String p1;

	@SerializedName("p2")
	private Long p2;

	public DemoJsonDto() {
	}

	public DemoJsonDto(String p1, Long p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	public String getP1() {
		return p1;
	}

	public void setP1(String p1) {
		this.p1 = p1;
	}

	public Long getP2() {
		return p2;
	}

	public void setP2(Long p2) {
		this.p2 = p2;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("p1", p1).append("p2", p2).toString();
	}

}
