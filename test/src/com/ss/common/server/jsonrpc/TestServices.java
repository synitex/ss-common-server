package com.ss.common.server.jsonrpc;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.ss.common.server.BigBen;
import com.ss.common.server.ServerJsonHelper;


public class TestServices {

	private static final Logger log = LoggerFactory.getLogger(TestServices.class);

	public static final String DEMO_SERVICE = "demo";

	@BeforeClass
	public static void setUp() {
		System.out.println("setUp");
	}

	@AfterClass
	public static void cleanUp() {
		System.out.println("clean");
	}

	@Test
	public void testServicesRegistration() {
		BigBen ben = new BigBen();
		DemoService demoService = new DemoServiceImpl();
		JsonRpcServices services = JsonRpcServices.get();
		services.registerService(DemoService.class, demoService);
		log.info("Services registered in " + ben.getElapsedTimeFormatted());
	}

	@Test
	public void testMethod1() {
		JsonRpcServices services = JsonRpcServices.get();
		services.performCall(DEMO_SERVICE, DemoService.METHOD_DEMO1, null);
	}

	@Test
	public void testMethod2() {
		// void demo2(Long longParam, long p2, String p3, Boolean p4, boolean p5, Integer p6, int p7);
		StringBuilder json = new StringBuilder();
		json.append("{");
			json.append("'" +  DemoService.PARAM_1 + "':123");
			json.append(",'" + DemoService.PARAM_2 + "':2");
			json.append(",'" + DemoService.PARAM_3 + "':'hello'");
			json.append(",'" + DemoService.PARAM_4 + "':true");
			json.append(",'" + DemoService.PARAM_5 + "':false");
			json.append(",'" + DemoService.PARAM_6 + "':123");
			json.append(",'" + DemoService.PARAM_7 + "':123");
		json.append("}");

		JsonObject jsonObject = ServerJsonHelper.parseJsonObject(json.toString());

		JsonRpcServices services = JsonRpcServices.get();
		services.performCall(DEMO_SERVICE, DemoService.METHOD_DEMO2, jsonObject);
	}
	
	@Test
	public void testMethod3() {
		// void demo3(List<Long> p1, List<String> p2, List<Boolean> p3, List<Integer> p4);
		StringBuilder json = new StringBuilder();
		json.append("{");
			json.append("'" +  DemoService.PARAM_1 + "':[123,123,123,123]");
			json.append(",'" + DemoService.PARAM_2 + "':[1,'2','3']");
			json.append(",'" + DemoService.PARAM_3 + "':[false, false, true]");
			json.append(",'" + DemoService.PARAM_4 + "':[1,2,3,4]");			
		json.append("}");

		JsonObject jsonObject = ServerJsonHelper.parseJsonObject(json.toString());

		JsonRpcServices services = JsonRpcServices.get();
		services.performCall(DEMO_SERVICE, DemoService.METHOD_DEMO3, jsonObject);
	}
	
	@Test
	public void testMethod4() {
		// void demo4(List<DemoJsonDto> p1, DemoJsonDto p2);
		StringBuilder json = new StringBuilder();
		json.append("{");
			json.append("'" +  DemoService.PARAM_1 + "':[{'p1':'param1','p2':1234},{'p1':'param2','p2':4321}]");
			json.append(",'" + DemoService.PARAM_2 + "':{'p1':'param1','p2':1234}");				
		json.append("}");

		JsonObject jsonObject = ServerJsonHelper.parseJsonObject(json.toString());

		JsonRpcServices services = JsonRpcServices.get();
		services.performCall(DEMO_SERVICE, DemoService.METHOD_DEMO4, jsonObject);
	}

}
