package core;

import java.util.List;

import models.Feed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import statics.Field;
import statics.OperationTypes;

public class ResponseFactory {
	public static JSONObject produceErrorJSON(String message) {
		return produceErrorJSON(OperationTypes.COMM_RESP, message);
	}
	
	public static JSONObject produceErrorJSON(String type, String message) {
		JSONObject jo = new JSONObject();
		try {
			jo.put(Field.OPERATION_TYPE, type);
			jo.put(Field.SUCCEED, false);
			jo.put(Field.MESSAGE, message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jo;
	}
	
	public static JSONObject produceSucceedJSON(String message) {
		return produceSucceedJSON(OperationTypes.COMM_RESP, message);
	}
	
	public static JSONObject produceSucceedJSON(String type, String message) {
		JSONObject jo = new JSONObject();
		try {
			jo.put(Field.OPERATION_TYPE, type);
			jo.put(Field.SUCCEED, true);
			jo.put(Field.MESSAGE, message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jo;
	}
	
	public static JSONObject produceFeedJSON(Feed feed) {
		JSONObject jo = new JSONObject();
		try {
			jo.put(Field.AUTHOR, feed.getAuthor());
			jo.put(Field.CONTENT, feed.getContent());
			jo.put(Field.DATETIME, feed.getDatetime());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jo;
	}
	
	public static JSONObject produceFeedListJSON(List<Feed> list) {
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		try {
			jo.put(Field.OPERATION_TYPE, OperationTypes.FEED_RESP);
			for(int i=0, n=list.size();i<n;++i)
				ja.put(produceFeedJSON(list.get(i)));
			jo.put(Field.FEEDS, ja);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jo;
	}
}