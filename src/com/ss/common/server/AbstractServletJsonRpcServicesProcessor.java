package com.ss.common.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
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
import com.ss.common.gwt.jsonrpc.shared.ErrorDto;
import com.ss.common.gwt.jsonrpc.shared.JsonDto;
import com.ss.common.gwt.jsonrpc.shared.JsonRpcConstants;
import com.ss.common.server.jsonrpc.JsonRpcServices;
import com.ss.common.server.services.ContextHolder;
import com.ss.common.server.services.MessagesService;

@SuppressWarnings("serial")
public abstract class AbstractServletJsonRpcServicesProcessor extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(AbstractServletJsonRpcServicesProcessor.class);

	public abstract void registerJsonRpcServices();

	@Override
	public void init(ServletConfig config) throws ServletException {
		BigBen bigBen = new BigBen();
		log.info("Register JSON-RPC services...");
		registerJsonRpcServices();
		log.info("" + JsonRpcServices.get().getServicesCount() + " registered in " + bigBen.getElapsedTimeFormatted());
	}

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

	private <T extends Class> Object getBean(T clazz) {
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

		// TODO:
	}

	private void writeGenericError() throws IOException {
		writeError("error.generic");
	}

	private void writeError(String errorKey) throws IOException {
		ContextHolder ctxHolder = (ContextHolder) getBean(ContextHolder.class);
		MessagesService messagesService = (MessagesService) getBean(MessagesService.class);

		ServiceContext ctx = ctxHolder.getContext();
		HttpServletResponse resp = ctx.getResponse();
		String errorMsg = messagesService.getMsg(MessagesService.PREFIX_SERVER, errorKey);
		writeResponse(ServerJsonHelper.toJson(new ErrorDto(errorMsg)), resp);
	}

	private void writeVoidResponse() throws IOException {
		ContextHolder ctxHolder = (ContextHolder) getBean(ContextHolder.class);
		writeResponse("{}", ctxHolder.getContext().getResponse());
	}

	private <T extends JsonDto> void writeResponse(List<T> jsonDtos) throws IOException {
		ContextHolder ctxHolder = (ContextHolder) getBean(ContextHolder.class);
		ServiceContext ctx = ctxHolder.getContext();
		HttpServletResponse response = ctx.getResponse();
		String json = jsonDtos == null || jsonDtos.isEmpty() ? "{}" : ServerJsonHelper.toJson(jsonDtos);
		writeResponse(json, response);
	}

	private void writeResponse(JsonDto jsonDto) throws IOException {
		ContextHolder ctxHolder = (ContextHolder) getBean(ContextHolder.class);
		ServiceContext ctx = ctxHolder.getContext();
		HttpServletResponse response = ctx.getResponse();
		String json = jsonDto == null ? "{}" : ServerJsonHelper.toJson(jsonDto);
		writeResponse(json, response);
	}

	private void writeResponse(String jsonString, HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json;charset=utf-8");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().write(jsonString);
	}

}
