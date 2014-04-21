package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Client {
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	public void start(String serverIP, int serverPORT) throws UnknownHostException, IOException {
		socket = new Socket(serverIP, serverPORT);

		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
	}
	
	public JSONObject sendRequestUtilResponse(final JSONObject jo) {
		try {
			oos.writeObject(jo.toString());
			String stringJSON = (String) ois.readObject();
			JSONObject response = new JSONObject(stringJSON);
			return response;
		} catch (IOException | ClassNotFoundException | JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void sendRequestWithoutWait(final JSONObject jo) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					// Send request
					oos.writeObject(jo.toString());
					
					// Receive response
					String stringJSON = (String) ois.readObject();
					JSONObject response = new JSONObject(stringJSON);
					
					// Call callback function
					callbackResponse(response);
				} catch (IOException | JSONException | ClassNotFoundException e) {
					e.printStackTrace();
				}
				
			}
			
		});
		thread.start();
	}
	
	public abstract void callbackResponse(JSONObject jo);
}
