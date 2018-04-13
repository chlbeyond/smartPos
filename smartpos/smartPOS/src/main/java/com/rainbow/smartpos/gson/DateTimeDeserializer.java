package com.rainbow.smartpos.gson;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class DateTimeDeserializer implements JsonDeserializer<Date> {

	@Override
	public Date deserialize(JsonElement arg0, Type arg1,
			JsonDeserializationContext arg2) throws JsonParseException {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return formater.parse(arg0.getAsString());
		} catch (ParseException e) {
		}
		formater = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return formater.parse(arg0.getAsString());
		} catch (ParseException e) {
		}
		formater = new SimpleDateFormat("HH:mm:ss");
		try {
			return formater.parse(arg0.getAsString());
		} catch (ParseException e) {
		}
		return null;
	}

}
