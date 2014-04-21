package core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import models.Feed;
import models.Subscribe;
import models.User;

import org.json.JSONException;
import org.json.JSONObject;

import database.DataAccess;
import server.Session;
import server.SessionContext;
import statics.Field;
import statics.OperationTypes;
import utilities.DatetimeHelper;

public class Controller {
	private DataAccess da;
	private SessionContext context;
	private Session session;

	public Controller(Session session, SessionContext context) {
		this.da = new DataAccess();
		this.context = context;
		this.session = session;
	}

	public JSONObject dispatch(JSONObject jo) {
		try {
			String opType = jo.getString(Field.OPERATION_TYPE);
			Method method = this.getClass().getDeclaredMethod(opType,
					JSONObject.class);
			return (JSONObject) method.invoke(this, jo);
		} catch (NullPointerException | JSONException | NoSuchMethodException
				| SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			return ResponseFactory.produceErrorJSON("Incorrect parameters.");
		}
	}

	protected JSONObject register(JSONObject jo) {
		try {
			String username = jo.getString(Field.USERNAME);
			String password = jo.getString(Field.PASSWORD);
			boolean succeed = da.createUser(username, password);
			if (succeed)
				return ResponseFactory.produceSucceedJSON(username);
			else
				return ResponseFactory
						.produceErrorJSON("Username already exists in database, please try again.");
		} catch (JSONException e) {
			return ResponseFactory.produceErrorJSON("Incorrect parameters.");
		}
	}

	protected JSONObject login(JSONObject jo) {
		try {
			String username = jo.getString(Field.USERNAME);
			String password = jo.getString(Field.PASSWORD);
			User user = da.getUser(username);
			if (user == null) {
				return ResponseFactory
						.produceErrorJSON("Account does not exist.");
			}
			if (user.getPassword().equals(password)) {
				if (context.logUserIn(session, username))
					return ResponseFactory.produceSucceedJSON(
							OperationTypes.LOGIN_RESP, username);
				else
					return ResponseFactory
							.produceErrorJSON("You already logged in somewhere.");
			} else
				return ResponseFactory
						.produceErrorJSON("Username and password does not match.");
		} catch (JSONException e) {
			return ResponseFactory.produceErrorJSON("Incorrect parameters.");
		}
	}

	protected JSONObject logout(JSONObject jo) {
		String username = context.logUserout(session);
		if (username == null)
			return ResponseFactory.produceErrorJSON("You have not logged in.");
		else
			return ResponseFactory.produceSucceedJSON(
					OperationTypes.LOGOUT_RESP, username);
	}

	protected JSONObject delete(JSONObject jo) {
		String username = context.getCurrentUser(session);
		if (username == null)
			return ResponseFactory.produceErrorJSON("You have not logged in.");
		else {
			synchronized (da) {
				if (da.getUser(username) == null)
					return ResponseFactory
							.produceErrorJSON("Account does not exist.");
				boolean succeed = da.deleteUser(username);
				if (succeed)
					return ResponseFactory.produceSucceedJSON(
							OperationTypes.LOGOUT_RESP, username);
				else
					return ResponseFactory.produceErrorJSON("Delete failed.");
			}
		}
	}

	protected JSONObject subscribe(JSONObject jo) {
		try {
			String username = context.getCurrentUser(session);
			if (username == null)
				return ResponseFactory
						.produceErrorJSON("You have not logged in.");
			else {
				String author = jo.getString(Field.AUTHOR);
				String datetime = jo.getString(Field.DATETIME);
				if ((datetime = DatetimeHelper.formatDatetimeString(datetime)) != null) {
					synchronized (da) {
						boolean succeed;
						if (da.getSubscribe(username, author) != null)
							succeed = da.updateSubscribe(username, author,
									datetime);
						else
							succeed = da.createSubscribe(username, author,
									datetime);
						if (succeed)
							return ResponseFactory.produceSucceedJSON(author);
						else
							return ResponseFactory
									.produceErrorJSON("Subscribe failed.");
					}
				} else
					return ResponseFactory
							.produceErrorJSON("Datetime format is incorrect.");
			}
		} catch (JSONException e) {
			return ResponseFactory.produceErrorJSON("Incorrect parameters.");
		}
	}

	protected JSONObject unsubscribe(JSONObject jo) {
		try {
			String username = context.getCurrentUser(session);
			if (username == null)
				return ResponseFactory
						.produceErrorJSON("You have not logged in.");
			else {
				String author = jo.getString(Field.AUTHOR);
				synchronized (da) {
					if (da.getSubscribe(username, author) == null)
						return ResponseFactory
								.produceErrorJSON("You have not subscribe this author.");
					boolean succeed = da.deleteSubscribe(username, author);
					if (succeed)
						return ResponseFactory.produceSucceedJSON(author);
					else
						return ResponseFactory
								.produceErrorJSON("Unsubscribe failed.");
				}
			}
		} catch (JSONException e) {
			return ResponseFactory.produceErrorJSON("Incorrect parameters.");
		}
	}

	protected JSONObject send(JSONObject jo) {
		try {
			String username = context.getCurrentUser(session);
			if (username == null)
				return ResponseFactory
						.produceErrorJSON("You have not logged in.");
			else {
				String content = jo.getString(Field.CONTENT);
				String datetime = DatetimeHelper.getNowDatetimeString();
				boolean succeed = da.createFeed(username, content, datetime);
				if (succeed)
					return ResponseFactory.produceSucceedJSON("Send time: "
							+ datetime);
				else
					return ResponseFactory.produceErrorJSON("Send failed.");
			}
		} catch (JSONException e) {
			return ResponseFactory.produceErrorJSON("Incorrect parameters.");
		}
	}

	protected JSONObject display(JSONObject jo) {
		String username = context.getCurrentUser(session);
		if (username == null)
			return ResponseFactory.produceErrorJSON("You have not logged in.");
		else {
			synchronized (da) {
				List<Feed> allFeeds = new ArrayList<Feed>();
				List<Subscribe> subs = da.getAllSubscribesOfReader(username);
				if (subs != null) {
					Subscribe subTemp;
					List<Feed> feedTemp;
					for (int i = 0, n = subs.size(); i < n; ++i) {
						subTemp = subs.get(i);
						feedTemp = da.getAllFeedsOfSubscribe(
								subTemp.getAuthor(), subTemp.getDatetime());
						da.updateSubscribe(username, subTemp.getAuthor(),
								DatetimeHelper.getNowDatetimeString());
						if (feedTemp != null)
							allFeeds.addAll(feedTemp);
					}
				}
				return ResponseFactory.produceFeedListJSON(allFeeds);
			}
		}
	}
}
