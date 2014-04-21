package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import statics.Configurations;

public class Server {
	private SessionContext context;
	private ServerSocket socket;
	private Thread thread;
	
	public void start() throws IOException {
		context = new SessionContext();
		socket = new ServerSocket(Configurations.SERVER_PORT);
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				do {
					try {
						final Socket clientSocket = socket.accept();
						final Session session = new Session(context, clientSocket);
						session.start();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} while(!socket.isClosed());
			}
			
		});
		thread.start();
	}
	
	public void stop() {
		
	}
}
