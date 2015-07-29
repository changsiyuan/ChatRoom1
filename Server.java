package chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

/*
 * server端处理程序
 */
public class Server {
	public static HashMap<String, ServerThread> users = new HashMap<String, ServerThread>();

	public static void main(String[] args) {
		try {
			System.out.println("server has start.");
			ServerSocket serverSocket = new ServerSocket(12345);

			// 程序会停在这里监听client端的连接请求
			while (true) {
				Socket socket = serverSocket.accept();
				ServerThread serverThread = new ServerThread(socket);
				serverThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
