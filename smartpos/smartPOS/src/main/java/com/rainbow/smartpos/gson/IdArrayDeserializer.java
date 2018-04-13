package com.rainbow.smartpos.gson;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;

import android.util.LongSparseArray;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class IdArrayDeserializer implements JsonDeserializer<LongSparseArray<?>> {

	@Override
	public LongSparseArray<?> deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		Type realType = ((ParameterizedType)typeOfT).getActualTypeArguments()[0];
		
		return parseAsArrayList(json, realType);
	}
 
	 @SuppressWarnings("unchecked")
	public <T> LongSparseArray<T> parseAsArrayList(JsonElement json, T type) {
		 LongSparseArray<T> newArray = new LongSparseArray<T>();
		Gson gson = GsonFactory.getGson();
 
		JsonArray array= json.getAsJsonArray();
		Iterator<JsonElement> iterator = array.iterator();
 
		while(iterator.hasNext()){
		    JsonElement json2 = (JsonElement)iterator.next();
			T object = (T) gson.fromJson(json2, (Class<?>)type);
			try {
				Field f = object.getClass().getField("id");
				f.setAccessible(true);
				long id = (Long)f.get(object);
				newArray.put(id,object);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
 
		return newArray;
	}

}
