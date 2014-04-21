package server;

import java.util.HashMap;
import java.util.Map;

public class SessionContext {
	private Map<Session, String> loggedInUsers;
	
	public SessionContext() {
		this.loggedInUsers = new HashMap<Session, String>();
	}
	
	public synchronized boolean logUserIn(Session session, String username) {
		if(loggedInUsers.containsValue(username))
			return false;
		loggedInUsers.put(session, username);
		return true;
	}
	
	public synchronized String logUserout(Session session) {
		if(loggedInUsers.containsKey(session))
			return loggedInUsers.remove(session);
		return null;
	}
	
	public synchronized String getCurrentUser(Session session) {
		if(loggedInUsers.containsKey(session))
			return loggedInUsers.get(session);
		return null;
	}
}
