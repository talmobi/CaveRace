package com.heartpirates;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;

public class Replay {

	long seed;
	int length;
	byte[] bytes;

	public int get(int n) {
		if (n >= length)
			return 100;
		return bytes[n];
	}

	public void save(String name) throws MalformedURLException, IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ByteBuffer buffer = ByteBuffer.allocate(1024);

		buffer.putLong(seed);
		buffer.putInt(length);

		for (int i = 0; i < bytes.length; i++) {
			if (!buffer.hasRemaining()) {
				buffer.flip();
				bout.write(buffer.array(), 0, buffer.limit());
				buffer.clear();
			}
			buffer.put(bytes[i]);
		}
		if (buffer.position() > 0) {
			buffer.flip();
			bout.write(buffer.array(), 0, buffer.limit());
			buffer.clear();
		}
		bout.flush();

		FileOutputStream out = new FileOutputStream(name);
		bout.writeTo(out);
		bout.flush();
	}

	public void save() throws MalformedURLException, IOException {
		save("replay");
	}

	public void load(String name) throws MalformedURLException, IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ByteBuffer buffer = ByteBuffer.allocate(1024);

		FileInputStream in = new FileInputStream(name);
		byte[] buf = new byte[1024];
		while (in.available() > 0) {
			int n = in.read(buf);
			if (n < 0)
				break;
			bout.write(buf, 0, n);
		}
		bout.flush();
		
		buffer = ByteBuffer.wrap(bout.toByteArray());
		
		this.seed = buffer.getLong();
		this.length = buffer.getInt();
		this.bytes = new byte[this.length];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = buffer.get();
		}
	}
}