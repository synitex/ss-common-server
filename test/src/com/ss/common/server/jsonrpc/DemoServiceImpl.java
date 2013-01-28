package com.ss.common.server.jsonrpc;

import java.util.Arrays;
import java.util.List;

public class DemoServiceImpl implements DemoService {

	@Override
	public void demo1() {
		System.out.println(": demo1 ");
	}

	@Override
	public Boolean demo2(Long longParam, long p2, String p3, Boolean p4, boolean p5, Integer p6, int p7) {
		System.out.println("demo2: " + printArgs(longParam, Long.valueOf(p2), p3, p4, Boolean.valueOf(p5), p6, Integer.valueOf(p7)));
		return true;
	}

	@Override
	public List<Integer> demo3(List<Long> p1, List<String> p2, List<Boolean> p3, List<Integer> p4) {
		System.out.println("demo3:" + printArgs(p1, p2, p3, p4));
		return Arrays.asList(1, 2, 3);
	}

	@Override
	public List<TestJsonDto> demo4(List<DemoJsonDto> p1, DemoJsonDto p2) {
		System.out.println("demo4: " + printArgs(p1, p2));
		return Arrays.asList(new TestJsonDto(), new TestJsonDto());
	}

	private String printArgs(Object... args) {
		return Arrays.toString(args);
	}

}
