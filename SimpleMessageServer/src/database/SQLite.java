package database;

import java.io.File;

import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteQueue;

public class SQLite {
	private static SQLite instance = null;
	private SQLiteQueue queue;
	
	private SQLite() {
		queue = new SQLiteQueue(new File("data.db"));
		queue.start();
	}
	
	public static synchronized SQLite getInstance() {
		if(instance == null)
			instance = new SQLite();
		return instance;
	}
	
	public <T,J extends SQLiteJob<T>> J execute(J job) {
		return queue.execute(job);
	}
}
