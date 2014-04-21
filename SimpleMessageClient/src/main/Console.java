package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import client.Client;
import statics.Configurations;
import statics.Field;
import statics.OperationTypes;
import utilities.DatetimeHelper;

public class Console {
	private BufferedReader br;
	private PrintStream out;

	private Client client;

	private boolean isLoggedIn;

	public Console() {
		this.br = new BufferedReader(new InputStreamReader(System.in));
		this.out = System.out;
		this.isLoggedIn = false;

		this.client = new Client() {

			@Override
			public void callbackResponse(JSONObject jo) {
				handleResponse(jo);
			}

		};
	}

	public JSONObject start() {
		while (true) {
			out.println("Please enter the server IP address.");
			out.println("Leave blank if you want to set IP as \""
					+ Configurations.SERVER_IP + "\".");
			String ip = inputString(0, 15);
			if ("".equals(ip))
				ip = Configurations.SERVER_IP;

			out.println("Please enter the server PORT number.");
			int port = inputInteger(0, 20000);

			try {
				client.start(ip, port);
				break;
			} catch (IOException e) {
				System.err.println("Unable connect to server.");
			}
		}

		while (true) {
			out.println("====================");
			out.println("Please type the index of operation and press enter.");
			if (!isLoggedIn) {
				out.println("1. Register a new account");
				out.println("2. Login");
				out.println("3. Exit the system");
				int index = inputInteger(1, 3);
				switch (index) {
				case 1:
					register();
					break;
				case 2:
					login();
					break;
				case 3:
					out.println("Bye.");
					System.exit(1);
					break;
				}
			} else {
				out.println("1. Send a feed");
				out.println("2. Display all feeds");
				out.println("3. Subscribe an author");
				out.println("4. Unsubscribe an author");
				out.println("5. Logout");
				out.println("6. Delete the account");
				out.println("7. Exit the system");
				int index = inputInteger(1, 7);
				switch (index) {
				case 1:
					send();
					break;
				case 2:
					display();
					break;
				case 3:
					subscribe();
					break;
				case 4:
					unsubscribe();
					break;
				case 5:
					logout();
					break;
				case 6:
					delete();
					break;
				case 7:
					out.println("Bye.");
					System.exit(1);
					break;
				}
			}
		}
	}

	public void handleResponse(JSONObject jo) {
		try {
			String op = jo.getString(Field.OPERATION_TYPE);
			if (op.equals(OperationTypes.COMM_RESP)) {
				boolean succeed = jo.getBoolean(Field.SUCCEED);
				String msg = jo.getString(Field.MESSAGE);
				if (succeed) {
					out.println("Succeed!");
				} else {
					out.println("Failed!");
				}
				out.println(msg);
			} else if (op.equals(OperationTypes.LOGIN_RESP)) {
				boolean succeed = jo.getBoolean(Field.SUCCEED);
				String msg = jo.getString(Field.MESSAGE);
				if (succeed) {
					isLoggedIn = true;
					out.println("Succeed!");
				} else {
					out.println("Failed!");
				}
				out.println(msg);
			} else if (op.equals(OperationTypes.LOGOUT_RESP)) {
				boolean succeed = jo.getBoolean(Field.SUCCEED);
				String msg = jo.getString(Field.MESSAGE);
				if (succeed) {
					isLoggedIn = false;
					out.println("Succeed!");
				} else {
					out.println("Failed!");
				}
				out.println(msg);
			} else if (op.equals(OperationTypes.FEED_RESP)) {
				JSONArray feeds = jo.getJSONArray(Field.FEEDS);
				for (int i = 0, n = feeds.length(); i < n; ++i) {
					JSONObject f = feeds.getJSONObject(i);
					out.print(f.getString(Field.AUTHOR));
					out.println(f.getString(Field.DATETIME));
					out.println(f.getString(Field.CONTENT));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void register() {
		try {
			out.println("====================");
			out.println("Please enter the username. Maximum 10 characters");
			String username = inputString(1, 10);
			out.println("Please enter the password. Maximum 10 characters");
			String password = inputString(1, 10);
			JSONObject jo = new JSONObject();
			jo.put(Field.OPERATION_TYPE, OperationTypes.REG_OP);
			jo.put(Field.USERNAME, username);
			jo.put(Field.PASSWORD, password);
			handleResponse(client.sendRequestUtilResponse(jo));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void login() {
		try {
			out.println("====================");
			out.println("Please enter the username. Maximum 10 characters");
			String username = inputString(1, 10);
			out.println("Please enter the password. Maximum 10 characters");
			String password = inputString(1, 10);
			JSONObject jo = new JSONObject();

			jo.put(Field.OPERATION_TYPE, OperationTypes.LOGIN_OP);
			jo.put(Field.USERNAME, username);
			jo.put(Field.PASSWORD, password);
			handleResponse(client.sendRequestUtilResponse(jo));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void send() {
		try {
			out.println("====================");
			out.println("Please enter your new feed. Maximum 100 characters");
			String feed = inputString(1, 100);
			JSONObject jo = new JSONObject();
			jo.put(Field.OPERATION_TYPE, OperationTypes.SEND_OP);
			jo.put(Field.CONTENT, feed);
			handleResponse(client.sendRequestUtilResponse(jo));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void display() {
		try {
			out.println("====================");
			out.println("Fetching feeds from server");
			JSONObject jo = new JSONObject();
			jo.put(Field.OPERATION_TYPE, OperationTypes.DSP_OP);
			handleResponse(client.sendRequestUtilResponse(jo));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void subscribe() {
		try {
			out.println("====================");
			out.println("Please enter the username of author you want to subscribe.");
			String author = inputString(1, 10);
			out.println("Please enter the start datetime of feed you want to receive from this author.");
			String datetime = inputDatetime();
			JSONObject jo = new JSONObject();
			jo.put(Field.OPERATION_TYPE, OperationTypes.SUB_OP);
			jo.put(Field.AUTHOR, author);
			jo.put(Field.DATETIME, datetime);
			handleResponse(client.sendRequestUtilResponse(jo));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void unsubscribe() {
		try {
			out.println("====================");
			out.println("Please enter the username of author you want to unsubscribe.");
			String author = inputString(1, 10);
			JSONObject jo = new JSONObject();
			jo.put(Field.OPERATION_TYPE, OperationTypes.UNSUB_OP);
			jo.put(Field.AUTHOR, author);
			handleResponse(client.sendRequestUtilResponse(jo));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void logout() {
		try {
			out.println("====================");
			JSONObject jo = new JSONObject();
			jo.put(Field.OPERATION_TYPE, OperationTypes.LOGOUT_OP);
			handleResponse(client.sendRequestUtilResponse(jo));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void delete() {
		try {
			out.println("====================");
			JSONObject jo = new JSONObject();
			jo.put(Field.OPERATION_TYPE, OperationTypes.DEL_OP);
			handleResponse(client.sendRequestUtilResponse(jo));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private String inputString(int minLength, int maxLength) {
		String str = "";
		try {
			while ((str = br.readLine()) != null) {
				if (str.length() < minLength)
					out.println("You type too less. Try again.");
				else if (str.length() > maxLength)
					out.println("You type too much. Try again.");
				else
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}

	private int inputInteger(int min, int max) {
		int value = 0;
		String str = "";
		try {
			while ((str = br.readLine()) != null) {
				try {
					value = Integer.parseInt(str);
					if (value < min || value > max)
						out.println("You should type some integer between "
								+ min + " and " + max + ".");
					else
						break;
				} catch (NumberFormatException e) {
					out.println("Please enter a valid integer. Try again.");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}

	private String inputDatetime() {
		String datetime = "";
		do {
			out.println("Please enter the datetime in this format: "
					+ Configurations.DATETIME_FORMAT);
			out.println("Leave blank if you want to set datetime as NOW.");
			try {
				datetime = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if ("".equals(datetime)) {
				datetime = DatetimeHelper.getNowDatetimeString();
				break;
			}
		} while ((datetime = DatetimeHelper.formatDatetimeString(datetime)) == null);
		return datetime;
	}

}
