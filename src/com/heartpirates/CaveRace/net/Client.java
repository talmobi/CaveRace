package com.heartpirates.CaveRace.net;

import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import com.heartpirates.CaveRace.Replay;

public class Client {

	ActionListener listener;

	List<Replay> replays = new LinkedList<Replay>();

	private final Object lock = new Object();
	private Network net;

	public static void main(String[] args) {
		try {
			new Client();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void test() throws Exception {
		Data data = getBogusData();
		Network net = new Network();
		System.out.print("Connecting...");
		Socket socket = new Socket(Network.HOST, Network.TCP_PORT);
		System.out.println(" DONE!");
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();
		System.out.println("Got outputstream...");
		byte[] bytes = net.toByteArray(data);
		String path = "/cgi-bin/CaveScores";
		String postRequest = "POST " + path + " HTTP/1.1\r\nHost: "
				+ Network.HOST + "\r\nContent-Length: " + bytes.length
				+ "\r\n\r\n";
		System.out.println("sending... ");
		os.write(postRequest.getBytes("UTF-8"));
		os.write(bytes);
		os.flush();
		System.out.println("sent!");

		int b;
		char l = 0;
		long now = System.currentTimeMillis();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while (is.available() > 0 || baos.size() < 1) {
			b = is.read();
			if (b == -1)
				break;
			if (System.currentTimeMillis() - now > 2000)
				break;
			char c = (char) b;
			if (c == '0' && l == '\n')
				break;
			baos.write(b);
			l = c;
		}
		baos.flush();
		System.out.println(new String(baos.toByteArray(), "UTF-8"));
		baos.close();

		System.out.println("Exit.");
		os.close();
		is.close();
		socket.close();
	}

	private Data getBogusData() {
		Data data = new Data();
		Replay[] replays = new Replay[20];

		for (int i = 0; i < replays.length; i++) {
			replays[i] = getBogusReplay();
			replays[i].name = "Anon[" + (char) (80 + Math.random() * 60) + "]"
					+ (int) (Math.random() * 100);
		}

		data.replays = replays;
		return data;
	}

	private Replay getBogusReplay() {
		Replay replay = new Replay();
		replay.bytes = new byte[(int) (120 * 30 + 20 * Math.random())];
		for (int i = 0; i < replay.bytes.length; i++) {
			replay.bytes[i] = (byte) i;
		}
		return replay;
	}

	public Client() {
	}

	public Client(Network net) {
		this.net = net;
	}

	public void sendAndUpdate(final Data data) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				synchronized (lock) {
					try {
						System.out.print("Connecting...");
						Socket socket = new Socket(Network.HOST,
								Network.TCP_PORT);
						System.out.println(" DONE!");
						InputStream is = socket.getInputStream();
						System.out.println("Got outputstream...");
						byte[] bytes = net.toByteArray(data);
						String path = "/cgi-bin/CaveScores?POST";
						String postRequest = "POST " + path
								+ " HTTP/1.1\r\nHost: " + Network.HOST
								+ "\r\nContent-Length: " + bytes.length
								+ "\r\n\r\n";
						System.out.println("sending... ");
						OutputStream os = socket.getOutputStream();
						os.write(postRequest.getBytes("UTF-8"));
						os.write(bytes);
						os.flush();
						System.out.println("sent!");

						is.close();
						os.close();
						socket.close();

						Thread.sleep(500);

						// send TOP message
						System.out.print("Connecting...");
						socket = new Socket(Network.HOST, Network.TCP_PORT);
						System.out.println(" DONE!");
						is = socket.getInputStream();
						System.out.println("Got outputstream...");
						path = "/cgi-bin/CaveScores?TOP";
						postRequest = "POST " + path + " HTTP/1.1\r\nHost: "
								+ Network.HOST + "\r\n\r\n";
						System.out.println("sending... ");
						os = socket.getOutputStream();
						os.write(postRequest.getBytes("UTF-8"));
						os.flush();

						// receive message
						int b;
						char l = 0;
						long now = System.currentTimeMillis();
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						StringBuilder sb = new StringBuilder();

						while (true) {
							b = is.read();
							char c = (char) b;
							sb.append(c);

							if (sb.toString().endsWith("\r\n\r\n")) {
								break;
							}
							l = c;
						}

						int pos = 0;
						sb = new StringBuilder();
						while (true) {
							b = is.read();
							pos++;
							char c = (char) b;
							sb.append(c);

							if (c == '\n' && l == '\r') { // end of headers
								String s = sb.substring(0, pos - 2);

								String[] split = s.split(":");
								int size = Integer.parseInt(split[1].trim());
								byte[] data = new byte[size];
								is.read(data);
								Data obj = net.toDataObject(data);
								System.out.println("GOT REPLAYS: "
										+ obj.replays.length);
								replays.clear();
								for (int i = 0; i < obj.replays.length; i++) {
									System.out.println("reading replay data");
									replays.add(obj.replays[i]);
								}
								System.out.println("UPDATING LISTENER");
								listener.actionPerformed(null);
								break;
							}
							l = c;
						}
						System.out.println("Exit.");
						os.close();
						is.close();
						socket.close();
					} catch (Exception e) {
					}
				}
			}

		}).start();
	}

	public void getTop() throws IOException {
		// send TOP message
		System.out.print("Connecting...");
		Socket socket = new Socket(Network.HOST, Network.TCP_PORT);
		System.out.println(" DONE!");
		InputStream is = socket.getInputStream();
		System.out.println("Got outputstream...");
		String path = "/cgi-bin/CaveScores?TOP";
		String postRequest = "GET " + path + " HTTP/1.1\r\nHost: "
				+ Network.HOST + "\r\n\r\n";
		System.out.println("sending... ");
		OutputStream os = socket.getOutputStream();
		os.write(postRequest.getBytes("UTF-8"));
		os.flush();

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// receive message
		int b;
		char l = 0;
		long now = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();

		while (true) {
			if (System.currentTimeMillis() - now > 5000) {
				System.out.println("Highscores timed out.");
				return;
			}

			if (is.available() < 1)
				continue;

			b = is.read();
			char c = (char) b;
			sb.append(c);

			if (sb.toString().endsWith("\r\n\r\n")) {
				break;
			}
			l = c;
		}

		int pos = 0;
		sb = new StringBuilder();
		while (true) {
			b = is.read();
			pos++;
			char c = (char) b;
			sb.append(c);

			if (c == '\n' && l == '\r') { // end of headers
				String s = sb.substring(0, pos - 2);
				System.out.println("[" + s + "]");

				String[] split = s.split(":");
				int size = Integer.parseInt(split[1].trim());
				System.out.println("SIZE: " + size);
				byte[] data = new byte[size];

				ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
				for (int i = 0; i < size; i++) {
					baos.write(is.read());
				}
				baos.flush();
				data = baos.toByteArray();
				baos.close();

				Data obj = net.toDataObject(data);
				System.out.println("GOT REPLAYS: " + obj.replays.length);
				replays.clear();
				for (int i = 0; i < obj.replays.length; i++) {
					System.out.println("reading replay data");
					replays.add(obj.replays[i]);
				}
				System.out.println("UPDATING LISTENER");
				if (listener != null)
					listener.actionPerformed(null);
				break;
			}
			l = c;
		}
		System.out.println("Exit.");
		os.close();
		is.close();
		socket.close();
	}

	public void send(final Data data) throws IOException {
		System.out.print("Connecting...");
		Socket socket = new Socket(Network.HOST, Network.TCP_PORT);
		System.out.println(" DONE!");
		InputStream is = socket.getInputStream();
		System.out.println("Got outputstream...");
		byte[] bytes = net.toByteArray(data);
		String path = "/cgi-bin/CaveScores?POST";
		String postRequest = "POST " + path + " HTTP/1.1\r\nHost: "
				+ Network.HOST + "\r\nContent-Length: " + bytes.length
				+ "\r\n\r\n";
		System.out.println("sending... ");
		OutputStream os = socket.getOutputStream();
		os.write(postRequest.getBytes("UTF-8"));
		os.write(bytes);
		os.flush();
		System.out.println("sent!");

		// receive message
		int b;
		char ltt = 0;
		char lt = 0;
		char l = 0;
		long now = System.currentTimeMillis();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		StringBuilder sb = new StringBuilder();

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		while (true) {
			if (System.currentTimeMillis() - now > 5000)
				break;

			if (is.available() < 1) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}

			b = is.read();
			char c = (char) b;
			sb.append(c);

			if (c == '\n' && l == '\r' && lt == '\n' && ltt == '\r') {
				break;
			}
			ltt = lt;
			lt = l;
			l = c;
		}

		System.out.println(sb.toString());

		System.out.println("Exit.");
		os.close();
		is.close();
		socket.close();
	}

	public void addListener(ActionListener l) {
		this.listener = l;
	}

	private void receiveMessage(InputStream is) throws IOException {
		int b;
		char l = 0;
		long now = System.currentTimeMillis();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while (is.available() > 0 || baos.size() < 1) {
			b = is.read();
			if (b == -1)
				break;
			if (System.currentTimeMillis() - now > 2000)
				break;
			char c = (char) b;
			if (c == '0' && l == '\n')
				break;
			baos.write(b);
			l = c;
		}
		baos.flush();
		System.out.println(new String(baos.toByteArray(), "UTF-8"));
		baos.close();
	}

	public List<Replay> getReplays() {
		return this.replays;
	}
}