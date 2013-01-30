package com.ss.common.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.JsonObject;
import com.ss.common.gwt.jsonrpc.shared.DataValidationDto;
import com.ss.common.gwt.jsonrpc.shared.ErrorDto;
import com.ss.common.gwt.jsonrpc.shared.JsonDto;
import com.ss.common.gwt.jsonrpc.shared.JsonRpcConstants;
import com.ss.common.server.jsonrpc.JsonRpcApplicationException;
import com.ss.common.server.jsonrpc.JsonRpcCallException;
import com.ss.common.server.jsonrpc.JsonRpcDataValidationException;
import com.ss.common.server.jsonrpc.JsonRpcLocalizedApplicationException;
import com.ss.common.server.jsonrpc.JsonRpcServices;
import com.ss.common.server.services.ContextHolder;
import com.ss.common.server.services.MessagesService;

@SuppressWarnings("serial")
public abstract class AbstractServletJsonRpcServicesProcessor extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(AbstractServletJsonRpcServicesProcessor.class);

	public abstract ErrorDto createGenericErrorDto();
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}

	private void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ContextHolder ctxHolder = (ContextHolder) getBean(ContextHolder.class);
		try {
			ctxHolder.init(req, resp);
			processRequest();
		} finally {
			ctxHolder.clear();
		}
	}

	public <T extends Class> Object getBean(T clazz) {
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		return wac.getBean(clazz);
	}

	public String getRequestParam(String name) {
		ContextHolder ctxHolder = (ContextHolder) getBean(ContextHolder.class);
		ServiceContext ctx = ctxHolder.getContext();
		if (ctx == null) {
			throw new RuntimeException("No service context is set!");
		}
		return ctx.getRequest().getParameter(name);
	}

	private void processRequest() throws IOException {
		ContextHolder ctxHolder = (ContextHolder) getBean(ContextHolder.class);
		ServiceContext ctx = ctxHolder.getContext();

		String method = getRequestParam(JsonRpcConstants.METHOD);
		String data = getRequestParam(JsonRpcConstants.DATA);

		if (StringUtils.isEmpty(method)) {
			log.error("No serviceId and methodId is detected in request.");
			writeGenericError();
			return;
		}

		JsonObject jsonData = null;
		if (!StringUtils.isEmpty(data)) {
			jsonData = ServerJsonHelper.parseJsonObject(data);
		}

		int idx = method.indexOf(".");
		if (idx < 1) {
			log.error("No serviceId and methodId is detected in request.");
			writeGenericError();
			return;
		}
		
		String serviceId = method.substring(0, idx);
		String methodId = method.substring(idx + 1);

		JsonRpcServices services = JsonRpcServices.get();
		String resultJson = null;

		try {
			// TODO: check if current user is logged in
			resultJson = services.performCall(serviceId, methodId, jsonData);

		} catch (Exception e) {
			
			if (e instanceof JsonRpcLocalizedApplicationException) {

				JsonRpcLocalizedApplicationException casted = (JsonRpcLocalizedApplicationException) e;
				String key = casted.getMessageKey();
				String prefix = casted.getMessagePrefix();
				
				MessagesService messagesService = (MessagesService) getBean(MessagesService.class);
				String msg = messagesService.getMsg(prefix, key);

				ErrorDto errorDto = new ErrorDto(casted.getId(), key, prefix, msg);
				writeError(errorDto);
				return;
				
			} else if (e instanceof JsonRpcDataValidationException) {

				JsonRpcDataValidationException casted = (JsonRpcDataValidationException) e;
				String id = casted.getId();
				List<DataValidationDto> validations = casted.getValidations();
				writeError(new ErrorDto(id, validations));
				return;

			} else if (e instanceof JsonRpcApplicationException) {
				
				JsonRpcApplicationException casted = (JsonRpcApplicationException) e;
				String id = casted.getId();
				writeError(new ErrorDto(id));
				return;
				
			} else if (e instanceof JsonRpcCallException) {
				
				writeGenericError();
				return;
				
			} else {
				
				log.error("Failed to perform call to Json-Rpc service.", e);
				throw new RuntimeException(e);
				
			}
		}

		if (resultJson == null || "".equals(resultJson)) {
			writeVoidResponse();
		} else {
			writeResponseData(resultJson, ctx.getResponse());
		}

	}

	private void writeGenericError() throws IOException {
		ErrorDto errorDto = createGenericErrorDto();
		writeError(errorDto);
	}

	private void writeError(ErrorDto errorDto) throws IOException {
		ContextHolder ctxHolder = (ContextHolder) getBean(ContextHolder.class);
		ServiceContext ctx = ctxHolder.getContext();
		HttpServletResponse resp = ctx.getResponse();
		writeResponseError(ServerJsonHelper.toJson(errorDto), resp);
	}

	private void writeVoidResponse() throws IOException {
		ContextHolder ctxHolder = (ContextHolder) getBean(ContextHolder.class);
		writeResponseData("{}", ctxHolder.getContext().getResponse());
	}

	private <T extends JsonDto> void writeResponse(List<T> jsonDtos) throws IOException {
		ContextHolder ctxHolder = (ContextHolder) getBean(ContextHolder.class);
		ServiceContext ctx = ctxHolder.getContext();
		HttpServletResponse response = ctx.getResponse();
		String json = jsonDtos == null || jsonDtos.isEmpty() ? "{}" : ServerJsonHelper.toJson(jsonDtos);
		writeResponseData(json, response);
	}

	private void writeResponse(JsonDto jsonDto) throws IOException {
		ContextHolder ctxHolder = (ContextHolder) getBean(ContextHolder.class);
		ServiceContext ctx = ctxHolder.getContext();
		HttpServletResponse response = ctx.getResponse();
		String json = jsonDto == null ? "{}" : ServerJsonHelper.toJson(jsonDto);
		writeResponseData(json, response);
	}

	private void writeResponseError(String errorJson, HttpServletResponse resp) throws IOException {
		String json = "{\"" + JsonRpcConstants.ERROR + "\":" + errorJson + "}";
		writeResponseString(json, resp);
	}

	private void writeResponseData(String jsonData, HttpServletResponse resp) throws IOException {
		String json = "{\"" + JsonRpcConstants.DATA + "\":" + jsonData + "}";
		writeResponseString(json, resp);
	}

	private void writeResponseString(String str, HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json;charset=utf-8");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().write(str);
	}

}
