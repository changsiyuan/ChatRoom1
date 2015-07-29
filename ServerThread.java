package chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;

/*
 * 服务器端多线程处理程序
 */
public class ServerThread extends Thread {
	private Socket socket = null;

	private String data = null; // 存储本人姓名
	private String message = null; // 存储本人发送的信息

	private InputStream is = null;
	private InputStreamReader isr = null;
	private BufferedReader br = null;

	private OutputStream os = null;
	private PrintWriter pw = null;

	public ServerThread(Socket socket) {
		this.socket = socket;

		try {
			is = socket.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);

			os = socket.getOutputStream();
			pw = new PrintWriter(os);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {

		try {

			boolean b = true;
			do {
				data = br.readLine();

				if (!data.contains(":")) {
					pw.println("登录信息格式不正确！请重新登陆！");
					pw.flush();
					b = false;
				} else {
					String[] s = data.split(":");
					if (s[0].equals("login")) {
						// 验证用户是否已经存在
						if (!Server.users.containsKey(s[1])) {
							Server.users.put(s[1], this);
							pw.println("登陆成功！欢迎您！");
							pw.flush();
							sendAll(data.split(":")[1], data.split(":")[1] + "已经登录.");
							b = true;
						} else {
							pw.println("用户名已经存在！请重新登陆！");
							pw.flush();
							b = false;
						}
					} else {
						pw.println("登录信息格式不正确！请重新登陆！");
						pw.flush();
						b = false;
					}
				}
			} while (!b);

			while (true) {
				message = br.readLine();

				if (message.equals("who")) {
					pw.println("This is all the users online now:");
					pw.flush();
					Iterator iter = Server.users.entrySet().iterator();
					while (iter.hasNext()) {
						pw.println(iter.next().toString().split("=")[0]);
						pw.flush();
					}
				} else if (message.equals("help")) {
					pw.println("如需帮助请询问管理员！");
					pw.flush();
				} else if (message.equals("quit")) {
					Server.users.remove(data);
					pw.println("您已经退出登录！");
					pw.flush();
					sendAll(data.split(":")[1], data.split(":")[1] + " 已经退出登录.");
					break;
				} else {
					if (!message.contains(":")) {
						pw.println("您输入的聊天信息格式错误！请重新输入！");
						pw.flush();
					} else {
						String[] s = message.split(":");
						if (s[0].equals("all")) {
							sendAll(data.split(":")[1], s[1]);
							pw.println("您对所有人说：" + s[1]);
							pw.flush();
						} else if (s[0].equals(data.split(":")[1])) {
							pw.println("您不能和自己聊天！");
							pw.flush();
						} else {
							if (!Server.users.containsKey(s[0])) {
								pw.println("聊天对象尚未登录！");
								pw.flush();
							} else {
								pw.println("您对" + s[0] + "说：" + s[1]);
								pw.flush();
								sendSomeone(s[0], s[1]);
							}
						}
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				isr.close();
				is.close();

				pw.close();
				os.close();

				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void sendAll(String hisOwnName, String msg) {
		Iterator iter = Server.users.entrySet().iterator();
		while (iter.hasNext()) {
			String name = iter.next().toString().split("=")[0];
			if (name.equals(hisOwnName)) {

			} else {
				sendSomeone(name, msg);
			}
		}
	}

	public void sendSomeone(String name, String msg) {
		Server.users.get(name).pw.println(data.split(":")[1] + " send you message: " + msg);
		Server.users.get(name).pw.flush();
	}
}
