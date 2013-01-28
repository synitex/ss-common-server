package com.ss.common.server.jsonrpc;

import java.util.Arrays;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.ss.common.gwt.jsonrpc.shared.JsonDto;

public class TestJsonDto implements JsonDto {

	@SerializedName("pd")
	private DemoJsonDto demo;

	@SerializedName("pdl")
	private List<DemoJsonDto> demoList;

	@SerializedName("aaa")
	private Integer aaa;

	public TestJsonDto() {
		demo = new DemoJsonDto("My demo", 2L);
		demoList = Arrays.asList(new DemoJsonDto("My demo1", 5L), new DemoJsonDto("My demo2", 29999L));
		aaa = 5;
	}

}
