package database;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteStatement;

import models.Feed;
import models.Subscribe;
import models.User;

public class DataAccess {
	private static final String USER_TABLE_NAME = "user";
	private static final String SUBSCRIBE_TABLE_NAME = "subscribe";
	private static final String FEED_TABLE_NAME = "feed";

	public synchronized boolean createUser(final String username,
			final String password) {
		SQLiteJob<Void> job = new SQLiteJob<Void>() {

			@Override
			protected Void job(SQLiteConnection connection) throws Throwable {
				connection.exec("INSERT INTO " + USER_TABLE_NAME + " VALUES('"
						+ username + "', '" + password + "')");
				return null;
			}

		};
		try {
			SQLite.getInstance().execute(job).get();
			return true;
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return false;
		}
	}

	public synchronized boolean deleteUser(final String username) {
		SQLiteJob<User> job = new SQLiteJob<User>() {

			@Override
			protected User job(SQLiteConnection connection) throws Throwable {
				connection.exec("DELETE FROM " + USER_TABLE_NAME
						+ " WHERE username = '" + username + "'");
				return null;
			}

		};
		try {
			SQLite.getInstance().execute(job).get();
			return true;
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return false;
		}
	}

	public synchronized User getUser(final String username) {
		SQLiteJob<User> job = new SQLiteJob<User>() {

			@Override
			protected User job(SQLiteConnection connection) throws Throwable {
				SQLiteStatement st = connection.prepare("SELECT * FROM "
						+ USER_TABLE_NAME + " WHERE username = '" + username
						+ "'");
				User user = st.step() ? new User(st.columnString(0),
						st.columnString(1)) : null;
				return user;
			}

		};
		return SQLite.getInstance().execute(job).complete();
	}

	public synchronized boolean createSubscribe(final String reader,
			final String author, final String datetime) {
		SQLiteJob<Void> job = new SQLiteJob<Void>() {

			@Override
			protected Void job(SQLiteConnection connection) throws Throwable {
				connection.exec("INSERT INTO " + SUBSCRIBE_TABLE_NAME
						+ " VALUES('" + reader + "', '" + author + "', '"
						+ datetime + "')");
				return null;
			}

		};
		try {
			SQLite.getInstance().execute(job).get();
			return true;
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return false;
		}
	}

	public synchronized boolean deleteSubscribe(final String reader,
			final String author) {
		SQLiteJob<Void> job = new SQLiteJob<Void>() {

			@Override
			protected Void job(SQLiteConnection connection) throws Throwable {
				connection.exec("DELETE FROM " + SUBSCRIBE_TABLE_NAME
						+ " WHERE reader = '" + reader + "' AND author = '"
						+ author + "'");
				return null;
			}

		};
		try {
			SQLite.getInstance().execute(job).get();
			return true;
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return false;
		}
	}

	public synchronized Subscribe getSubscribe(final String reader,
			final String author) {
		SQLiteJob<Subscribe> job = new SQLiteJob<Subscribe>() {

			@Override
			protected Subscribe job(SQLiteConnection connection)
					throws Throwable {
				SQLiteStatement st = connection.prepare("SELECT * FROM "
						+ SUBSCRIBE_TABLE_NAME + " WHERE reader = '" + reader
						+ "' AND author = '" + author + "'");
				Subscribe subscribe = st.step() ? new Subscribe(
						st.columnString(0), st.columnString(1),
						st.columnString(2)) : null;
				return subscribe;
			}

		};
		return SQLite.getInstance().execute(job).complete();
	}

	public synchronized List<Subscribe> getAllSubscribesOfReader(
			final String reader) {
		SQLiteJob<List<Subscribe>> job = new SQLiteJob<List<Subscribe>>() {

			@Override
			protected List<Subscribe> job(SQLiteConnection connection)
					throws Throwable {
				SQLiteStatement st = connection.prepare("SELECT * FROM "
						+ SUBSCRIBE_TABLE_NAME + " WHERE reader = '" + reader
						+ "'");
				List<Subscribe> list = new ArrayList<Subscribe>();
				while (st.step()) {
					list.add(new Subscribe(st.columnString(0), st
							.columnString(1), st.columnString(2)));
				}
				return list;
			}

		};
		return SQLite.getInstance().execute(job).complete();
	}

	public synchronized boolean updateSubscribe(final String reader,
			final String author, final String datetime) {
		SQLiteJob<Void> job = new SQLiteJob<Void>() {

			@Override
			protected Void job(SQLiteConnection connection) throws Throwable {
				connection.exec("UPDATE " + SUBSCRIBE_TABLE_NAME
						+ " SET datetime = datetime('" + datetime
						+ "') WHERE reader = '" + reader + "' AND author = '"
						+ author + "'");
				return null;
			}

		};
		try {
			SQLite.getInstance().execute(job).get();
			return true;
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return false;
		}
	}

	public synchronized boolean createFeed(final String author,
			final String content, final String datetime) {
		SQLiteJob<Void> job = new SQLiteJob<Void>() {

			@Override
			protected Void job(SQLiteConnection connection) throws Throwable {
				connection.exec("INSERT INTO " + FEED_TABLE_NAME + " VALUES('"
						+ author + "', '" + content + "', '" + datetime + "')");
				return null;
			}

		};
		try {
			SQLite.getInstance().execute(job).get();
			return true;
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return false;
		}
	}

	public synchronized List<Feed> getAllFeedsOfSubscribe(final String author, final String datetime) {
		SQLiteJob<List<Feed>> job = new SQLiteJob<List<Feed>>() {

			@Override
			protected List<Feed> job(SQLiteConnection connection)
					throws Throwable {
				SQLiteStatement st = connection.prepare("SELECT * FROM "
						+ FEED_TABLE_NAME + " WHERE author = '" + author
						+ "' AND datetime >= datetime('" + datetime + "')");
				List<Feed> list = new ArrayList<Feed>();
				while (st.step()) {
					list.add(new Feed(st.columnString(0), st.columnString(1),
							st.columnString(2)));
				}
				return list;
			}

		};
		return SQLite.getInstance().execute(job).complete();
	}
}
