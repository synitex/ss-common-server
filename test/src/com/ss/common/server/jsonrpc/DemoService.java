package com.ss.common.server.jsonrpc;

import java.util.List;

@JsonRpcService(TestServices.DEMO_SERVICE)
public interface DemoService {

	public static final String METHOD_DEMO1 = "md1";
	public static final String METHOD_DEMO2 = "md2";
	public static final String METHOD_DEMO3 = "md3";
	public static final String METHOD_DEMO4 = "md4";
	
	public static final String PARAM_1 = "pl";
	public static final String PARAM_2 = "p2";
	public static final String PARAM_3 = "p3";
	public static final String PARAM_4 = "p4";
	public static final String PARAM_5 = "p5";
	public static final String PARAM_6 = "p6";
	public static final String PARAM_7 = "p7";

	@JsonRpcServiceMethod(methodId = METHOD_DEMO1)
	void demo1();

	@JsonRpcServiceMethod(methodId = METHOD_DEMO2, paramIds = { PARAM_1, PARAM_2, PARAM_3, PARAM_4, PARAM_5, PARAM_6, PARAM_7 })
	Boolean demo2(Long longParam, long p2, String p3, Boolean p4, boolean p5, Integer p6, int p7);

	@JsonRpcServiceMethod(methodId = METHOD_DEMO3, paramIds = { PARAM_1, PARAM_2, PARAM_3, PARAM_4})
	List<Integer> demo3(List<Long> p1, List<String> p2, List<Boolean> p3, List<Integer> p4);

	@JsonRpcServiceMethod(methodId = METHOD_DEMO4, paramIds = { PARAM_1, PARAM_2 })
	List<TestJsonDto> demo4(List<DemoJsonDto> p1, DemoJsonDto p2);
}
