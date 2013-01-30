package com.ss.common.server.jsonrpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JsonRpcServiceMethod {	
	
	String methodId();

	String[] paramIds() default "";
	
	boolean isPublic() default true;

}
