package com.ss.common.server.jsonrpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.ss.common.gwt.jsonrpc.shared.JsonDto;
import com.ss.common.server.ServerJsonHelper;

/**
 * Entry point to JSON-RPC services. 
 * Use this class to register application JSON-RPC services at application startup time and to perform calls to registered services.
 * @author sergey.sinica
 *
 */
public class JsonRpcServices {

	private static final Logger log = LoggerFactory.getLogger(JsonRpcServices.class);

	private static final Map<Class, Class> supportedPrimitiveTypes = new HashMap<Class, Class>();
	static {
		supportedPrimitiveTypes.put(Long.TYPE, Long.class);
		supportedPrimitiveTypes.put(Integer.TYPE, Integer.class);
		supportedPrimitiveTypes.put(Boolean.TYPE, Boolean.class);
	}
	private static final Set<Class> supportedTypes = new HashSet<Class>();
	static {
		supportedTypes.add(String.class);
		supportedTypes.add(Integer.class);
		supportedTypes.add(Long.class);
		supportedTypes.add(Boolean.class);
		supportedTypes.add(JsonDto.class);
	}

	private static final JsonRpcServices INSTANCE = new JsonRpcServices();
	
	public static JsonRpcServices get() {
		return INSTANCE;
	}

	private Map<String, ServiceMeta> services = new HashMap<String, JsonRpcServices.ServiceMeta>();

	private JsonRpcServices() {

	}

	/**
	 * Registers json-rpc service.
	 * @param classOfInterface Service interface annotated with {@link JsonRpcService}
	 * @param instance Instance of service.
	 * @throws IllegalArgumentException if service metadata is incomplete or incorrect.
	 */
	public synchronized void registerService(Class classOfInterface, Object instance) {
		registerServiceImpl(classOfInterface, instance);
	}

	/**
	 * Performs json-rpc service call.
	 * @param serviceId Service id, which is specified thru {@link JsonRpcService} annotation.
	 * @param methodId Method id
	 * @param jsonArgs Method arguments as JSON.
	 * @return Result as JSON string or null, if void result.
	 * @throws IllegalArgumentException, JsonRpcServiceCallException
	 */
	public String performCall(String serviceId, String methodId, JsonObject jsonArgs) {

		ServiceMeta sm = services.get(serviceId);
		MethodMeta mm = sm.getMethods().get(methodId);
		Object instance = sm.getInstance();

		if (log.isDebugEnabled()) {
			log.debug("Performing call to " + serviceId + "." + methodId + " with json arg: " + (jsonArgs == null ? "null" : jsonArgs.toString()));
		}

		// parse arguments
		Object[] args = parseInputArguments(serviceId, mm, jsonArgs);

		if (log.isDebugEnabled()) {
			log.debug("......Parsed args: " + Arrays.toString(args));
		}

		// perform invocation
		Object result = performCallImpl(serviceId, mm, instance, args);


		// prepare result
		String jsonResult = ServerJsonHelper.toJson(result);

		if (log.isDebugEnabled()) {
			log.debug("......Call performed and result is: " + result);
			log.debug("......Result transformed to json: " + jsonResult);
		}

		return jsonResult;
	}

	public int getServicesCount() {
		return services.size();
	}

	private Object performCallImpl(String serviceId, MethodMeta mm, Object instance, Object[] args) {
		try {
			Method method = mm.getMethod();
			return args == null ? method.invoke(instance) : method.invoke(instance, args);
		} catch (IllegalArgumentException e) {
			throw new JsonRpcCallException("Failed to call service " + serviceId + "." + mm.getMethodId() + ".", e);
		} catch (IllegalAccessException e) {
			throw new JsonRpcCallException("Failed to call service " + serviceId + "." + mm.getMethodId() + ".", e);
		} catch (InvocationTargetException e) {
			throw new JsonRpcCallException("Failed to call service " + serviceId + "." + mm.getMethodId() + ".", e);
		}
	}

	private Object[] parseInputArguments(String serviceId, MethodMeta mm, JsonObject jsonArgs) {
		List<ArgMeta> argsMetas = mm.getArgs();
		Object[] args = null;
		if (argsMetas != null && argsMetas.size() > 0) {
			if (jsonArgs == null) {
				throw new IllegalArgumentException("Method " + mm.getMethodId() + " in service " + serviceId + " have " + argsMetas.size() + " params, but no json is passed in.");
			}
			args = new Object[argsMetas.size()];
			for (int i = 0; i < argsMetas.size(); i++) {
				Object obj = parseArg(argsMetas.get(i), jsonArgs);
				args[i] = obj;
			}
		}
		return args;
	}

	private void registerServiceImpl(Class classOfInterface, Object instance) {

		if (log.isDebugEnabled()) {
			log.debug("Registering service " + classOfInterface + " with instance " + instance.getClass());
		}

		String serviceId = parseServiceId(classOfInterface);

		if (log.isDebugEnabled()) {
			log.debug("Parsed service id=" + serviceId + " for " + classOfInterface);
		}

		List<MethodMeta> methods = parseMethods(classOfInterface);

		saveServiceMeta(serviceId, instance, methods);

	}

	private void saveServiceMeta(String serviceId, Object instance, List<MethodMeta> methods) {

		ServiceMeta meta = new ServiceMeta();
		meta.setInstance(instance);
		meta.setServiceId(serviceId);
		
		for (MethodMeta mm : methods) {
			meta.getMethods().put(mm.getMethodId(), mm);
		}

		if (services.containsKey(serviceId)) {
			throw new IllegalArgumentException("Service with id " + serviceId + " already registered.");
		}

		services.put(serviceId, meta);

		log.info("Service '" + serviceId + "' for " + meta.getInstance().getClass().getSimpleName() + " is registered.");
	}

	private List<MethodMeta> parseMethods(Class classOfInterface) {
		List<MethodMeta> result = new ArrayList<JsonRpcServices.MethodMeta>();
		Method[] methods = classOfInterface.getMethods();
		if (methods != null && methods.length > 0) {
			for (Method method : methods) {
				parseMethod(method, result);
			}
		}
		return result;
	}

	private void parseMethod(Method method, List<MethodMeta> methods) {
		JsonRpcServiceMethod methodAnno = method.getAnnotation(JsonRpcServiceMethod.class);
		if (methodAnno == null) {
			log.warn("Method " + method.getName() + " do not annotated with " + JsonRpcServiceMethod.class.getSimpleName() + ". Will ignore.");
			return;
		}
		String methodId = methodAnno.methodId();

		Type[] gtypes = method.getGenericParameterTypes();
		Class<?>[] types = method.getParameterTypes();
		String[] paramIds = methodAnno.paramIds();

		int declaredArgsCount = paramIds == null || paramIds.length == 1 && "".equals(paramIds[0]) ? 0 : paramIds.length;
		int actulaArgsCount = gtypes == null ? 0 : gtypes.length;
		if (declaredArgsCount != actulaArgsCount) {
			throw new IllegalArgumentException("Declared arguments count do not match actual argument count for method " + method.getName());
		}

		MethodMeta methodMeta = new MethodMeta();
		methodMeta.setMethod(method);
		methodMeta.setMethodId(methodId);

		// parse arguments
		if (actulaArgsCount > 0) {
			List<ArgMeta> argMetas = new ArrayList<ArgMeta>(actulaArgsCount);
			for (int i = 0; i < actulaArgsCount; i++) {
				ArgMeta am = parseArg(paramIds[i], gtypes[i], types[i]);
				if(argMetas.contains(am)) {
					throw new IllegalArgumentException("Duplicate argument " + am.getArgId() + " is detected in method " + methodId);
				}
				argMetas.add(am);
			}
			methodMeta.setArgs(argMetas);
 		}

		// parse return type
		Class<?> returnType = method.getReturnType();
		Type greturnType = method.getGenericReturnType();
		if (returnType != void.class) {
			ArgMeta returnMeta = parseArg("", greturnType, returnType);
			methodMeta.setResult(returnMeta);
		}

		if (methods.contains(methodMeta)) {
			throw new IllegalArgumentException("Duplicate method id " + methodId + " detected!");
		}

		if (log.isDebugEnabled()) {
			log.debug("Parsed method " + method.getName() + " with id '" + methodMeta.getMethodId() + "'");
			String tab = "......";
			List<ArgMeta> args = methodMeta.getArgs();
			if(args == null || args.size() == 0) {
				log.debug(tab + " No arguments.");
			} else {
				for (int i = 0; i < args.size(); i++) {
					log.debug(tab + (i + 1) + ") " + args.get(i));
				}
			}
			ArgMeta res = methodMeta.getResult();
			log.debug(tab + "result: " + (res == null ? "Void" : res));
		}

		methods.add(methodMeta);
	}

	private ArgMeta parseArg(String argId, Type type, Class declaredClazz) {
		if (type instanceof Class) {
			
			Class clazz = (Class) type;
			if (clazz.isPrimitive()) {
				return parsePrimitiveType(argId, type);
			} else {
				return parseType(argId, clazz);
			}
			
		} else if (List.class.isAssignableFrom(declaredClazz) && ParameterizedType.class.isAssignableFrom(type.getClass())) {
			
			return parseListArg(argId, (ParameterizedType) type);
			
		} else {
			
			throw new IllegalArgumentException("Invalid service method parameter " + type);
			
		}
	}

	private ArgMeta parsePrimitiveType(String argId, Type type) {
		Class clazz = (Class) type;
		if(supportedPrimitiveTypes.containsKey(clazz)) {
			return new ArgMeta(argId, supportedPrimitiveTypes.get(clazz), false);
		} else {
			throw new IllegalArgumentException("Unsupported argument type " + clazz.getName() + " for argument with id " + argId);
		}
	}

	private ArgMeta parseType(String argId, Class clazz) {
		for (Class supportedType : supportedTypes) {
			if(supportedType.isAssignableFrom(clazz)) {
				return new ArgMeta(argId, clazz, false);
			}
		}
		throw new IllegalArgumentException("Unsupported argument type " + clazz.getName() + " for argument with id " + argId);
	}

	private ArgMeta parseListArg(String argId, ParameterizedType type) {
		Type[] actualTypes = type.getActualTypeArguments();
		if (actualTypes != null && actualTypes.length == 1) {
			Class clazz = (Class) actualTypes[0];
			ArgMeta meta = parseType(argId, clazz);
			meta.setIsList();
			return meta;
		} else {
			throw new IllegalArgumentException("Failed to resolve List argument generic type");
		}
	}

	@SuppressWarnings("unchecked")
	private String parseServiceId(Class clazz) {
		JsonRpcService serviceAnno = (JsonRpcService)clazz.getAnnotation(JsonRpcService.class);		
		if (serviceAnno == null) {
			throw new IllegalArgumentException(
					"Failed to parse service id for class "
							+ clazz.getName()
							+ ". No class annotation "
							+ JsonRpcService.class.getName() + " is found.");
		} else {
			return serviceAnno.value();
		}
	}

	private Object parseArg(ArgMeta argMeta, JsonObject json) {
		Class clazz = argMeta.getClazz();
		if (argMeta.isList()) {
			return ServerJsonHelper.parseGenericList(argMeta.getArgId(), clazz, json);			
		} else {
			return ServerJsonHelper.get(argMeta.getArgId(), clazz, json);
		}
	}

	// -------------------------------------------------------------------------------------

	private static class ServiceMeta {
		private Object instance;
		private String serviceId;
		private Map<String, MethodMeta> methods = new HashMap<String, JsonRpcServices.MethodMeta>();

		public Object getInstance() {
			return instance;
		}

		public void setInstance(Object instance) {
			this.instance = instance;
		}

		public String getServiceId() {
			return serviceId;
		}

		public void setServiceId(String serviceId) {
			this.serviceId = serviceId;
		}

		public Map<String, MethodMeta> getMethods() {
			return methods;
		}

		public void setMethods(Map<String, MethodMeta> methods) {
			this.methods = methods;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ServiceMeta) {
				ServiceMeta casted = (ServiceMeta) obj;
				return serviceId.equals(casted.getServiceId());
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return serviceId.hashCode();
		}

		@Override
		public String toString() {
		    return new ToStringBuilder(this)
		    	.append("serviceId", serviceId)
		    	.append("instance", instance.getClass().getSimpleName())
		    	.append("methods", methods)
		    	.toString();
		}
	}

	private static class MethodMeta {
		private String methodId;
		private Method method;
		private List<ArgMeta> args;
		private ArgMeta result;

		public String getMethodId() {
			return methodId;
		}

		public void setMethodId(String methodId) {
			this.methodId = methodId;
		}

		public Method getMethod() {
			return method;
		}

		public void setMethod(Method method) {
			this.method = method;
		}

		public List<ArgMeta> getArgs() {
			return args;
		}

		public void setArgs(List<ArgMeta> args) {
			this.args = args;
		}

		public void setResult(ArgMeta result) {
			this.result = result;
		}

		public ArgMeta getResult() {
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof MethodMeta) {
				MethodMeta casted = (MethodMeta) obj;
				return methodId.equals(casted.getMethodId());
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return methodId.hashCode();
		}

		@Override
		public String toString() {
		    return new ToStringBuilder(this)
		    	.append("methodId", methodId)
		    	.append("args", args)
		    	.append("result", result)
		    	.toString();
		}

	}

	private static class ArgMeta {
		private String argId;
		private Class clazz;
		private boolean isList = false;

		public ArgMeta(String argId, Class clazz, boolean isList) {
			this.argId = argId;
			this.clazz = clazz;
			this.isList = isList;
		}

		public String getArgId() {
			return argId;
		}

		public void setArgId(String argId) {
			this.argId = argId;
		}

		public Class getClazz() {
			return clazz;
		}

		public void setClazz(Class clazz) {
			this.clazz = clazz;
		}

		public boolean isList() {
			return isList;
		}

		public void setIsList() {
			this.isList = true;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ArgMeta) {
				ArgMeta casted = (ArgMeta) obj;
				return argId.equals(casted.getArgId());
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return argId.hashCode();
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this)
				.append("id", argId)
				.append("class", clazz.getSimpleName())
				.append("isList", isList)
				.toString();
		}

	}

}
