package com.rainbow.smartpos.gson;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;

import android.util.LongSparseArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

public class GsonFactory {

	private static Gson gson = null;

	public static Gson getGson() {
		if (gson == null) {
			gson = new GsonBuilder().serializeNulls().registerTypeAdapter(Collection.class, new CollectionDeserializer())
					.registerTypeAdapter(Date.class, new DateTimeDeserializer())
					.registerTypeAdapter(LongSparseArray.class, new IdArrayDeserializer())
					.registerTypeAdapter(Boolean.class, new BooleanDeserializer()).create();
		}
		return gson;
	}

	public static class BooleanDeserializer implements JsonDeserializer<Boolean> {
		@Override
		public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonPrimitive primitive = json.getAsJsonPrimitive();
			if (primitive.isBoolean()) {
				return Boolean.valueOf(primitive.getAsBoolean());
			}
			if (primitive.isNumber()) {
				if (primitive.getAsInt() == 0) {
					return Boolean.FALSE;
				}
				return Boolean.TRUE;
			}
			if (primitive.isString()) {
				if (primitive.getAsString().equalsIgnoreCase("yes") || primitive.getAsString().equalsIgnoreCase("true")
						|| primitive.getAsString().equals("T") || primitive.getAsString().equals("Y")|| !primitive.getAsString().equals("0")){
					return Boolean.TRUE;
				}else {
					return Boolean.FALSE;
				}
			}
			return Boolean.FALSE;

		}
	}
}
