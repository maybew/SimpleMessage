package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import core.Controller;
import utilities.CloseHelper;

public class Session extends Thread {
	private SessionContext context;
	private Socket clientSocket;
	private Controller controller;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	public Session(SessionContext context, Socket clientSocket) {
		this.context = context;
		this.clientSocket = clientSocket;
		this.controller = new Controller(this, context);
	}

	@Override
	public void run() {
		try {
			ois = new ObjectInputStream(clientSocket.getInputStream());
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			 do {
				 String stringJSON = (String) ois.readObject();
				 JSONObject inputJO = new JSONObject(stringJSON);
				 
				 JSONObject outputJO = controller.dispatch(inputJO);
				 oos.writeObject(outputJO.toString());
			 } while(!clientSocket.isClosed());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			CloseHelper.close(ois);
			CloseHelper.close(oos);
			CloseHelper.close(clientSocket);
			context.logUserout(this);
		}
	}
}
