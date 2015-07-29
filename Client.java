package chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) {
		Socket socket = null;
		OutputStream os = null;
		PrintWriter pw = null;
		Scanner sc = null;

		try {
			socket = new Socket("localhost", 12345);
			System.out.println("连接成功，请您登陆！(登录规则：login:您的用户名)");
			
			//由于同一个程序不能够同时完成“实时监听server端消息并显示”“实时监听用户在键盘的输入并发送给server”两个任务
			//故将前者交由另外一个线程去处理
			ClientThread clientThread = new ClientThread(socket);
			clientThread.start();
			
			// 发送键盘上输入的内容给server
			os = socket.getOutputStream();
			pw = new PrintWriter(os);
			sc = new Scanner(System.in);
			while (sc.hasNext()) {  //程序会停在这里监听用户在键盘的输入
				pw.println(sc.nextLine());
				pw.flush();
			}
			socket.shutdownOutput();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				sc.close();
				pw.close();
				os.close();

				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
