package com.ss.common.server.jsonrpc;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.LongSerializationPolicy;
import com.ss.common.gwt.jsonrpc.shared.JsonDto;


public class ServerJsonHelper {

	private static final Logger log = Logger.getLogger(ServerJsonHelper.class.getName());

	public static final Gson GSON = new GsonBuilder()
			.setLongSerializationPolicy(LongSerializationPolicy.STRING)
			.create();

	private ServerJsonHelper() {

	}

	public static String toJson(Object obj) {
		if (obj == null) {
			return "{}";
		}
		return GSON.toJson(obj);
	}

	public static JsonObject parseJsonObject(String jsonString) {
		JsonElement jsonElement = null;
		try {
			jsonElement = new JsonParser().parse(jsonString);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to parse json!", e);
			return null;
		}
		if (jsonElement == null) {
			log.log(Level.SEVERE, "request json is null!");
			return null;
		}
		JsonObject json = null;
		try {
			json = jsonElement.getAsJsonObject();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to parse json object", e);
			return null;
		}
		if (json == null) {
			log.log(Level.SEVERE, "json object is null!");
			return null;
		}
		return json;
	}

	public static String getString(String param, JsonObject json) {
		if (json == null || StringUtils.isEmpty(param)) {
			return "";
		}
		JsonElement member = json.get(param);
		if (member == null) {
			return "";
		}
		return member.getAsString();
	}

	public static <T extends JsonDto> T parse(String param, Class<T> clazz, JsonObject json) {
		JsonElement jsonEl = json.get(param);
		if (jsonEl == null) {
			return null;
		}
		try {
			T parsedValue = GSON.fromJson(jsonEl, clazz);
			return parsedValue;
		} catch (JsonSyntaxException e) {
			log.log(Level.SEVERE, "Failed to parse json", e);
			return null;
		}
	}

	public static List<String> parseStrings(String param, JsonObject json) {
		final List<String> values = new ArrayList<String>();
		parseList(param, json, new Callback() {
			@Override
			public void parseItem(JsonElement el) {
				values.add(el.getAsString());
			}
		});
		return values;
	}

	public static <T extends JsonDto> List<T> parseList(String param, final Class<T> clazz, JsonObject json) {
		final List<T> values = new ArrayList<T>();
		parseList(param, json, new Callback() {
			@Override
			public void parseItem(JsonElement el) {
				T parsedValue = GSON.fromJson(el, clazz);
				values.add(parsedValue);
			}
		});
		return values;
	}
	
	public static void parseList(String param, JsonObject json, Callback callback) {
		JsonElement jsonEl = json.get(param);
		if (jsonEl == null) {
			return;
		}
		JsonArray array = jsonEl.getAsJsonArray();
		if (array == null) {
			return;
		}
		int size = array.size();
		for (int i = 0; i < size; i++) {
			JsonElement el = array.get(i);
			callback.parseItem(el);
		}
	}

	public static interface Callback {
		void parseItem(JsonElement el);
	}

}
