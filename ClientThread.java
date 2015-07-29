package chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientThread extends Thread {
	private Socket socket = null;
	private String info = null;
	private InputStream is = null;
	private InputStreamReader isr = null;
	private BufferedReader br = null;

	public ClientThread(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			is = socket.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			while ((info = br.readLine()) != null) {  //程序会停在这里监听server端消息并显示
				System.out.println(info);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				isr.close();
				is.close();

				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
