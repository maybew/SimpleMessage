package main;

import java.io.IOException;

import server.Server;

public class SimpleMessageServer {
	public static void main(String args[]) {
		Server server = new Server();
		try {
			server.start();
		} catch (IOException e) {
			System.out.println("Unable to start server!");
		}
	}
}
