package com.heartpirates.CaveRace.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.heartpirates.CaveRace.Replay;

public class Network {
	public static final int TCP_PORT = 80;
	public static final String HOST = "heartpirates.com";
	private final Kryo kryo;

	public Network() {
		kryo = new Kryo();
		kryo.register(Data.class);
	}

	public byte[] toByteArray(Data data) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzipos = new GZIPOutputStream(baos);
		Output output = new Output(gzipos);

		kryo.writeObject(output, data);
		output.close();

		return baos.toByteArray();
	}

	public Data toDataObject(byte[] bytes) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		GZIPInputStream gzipis = new GZIPInputStream(bais);
		Input input = new Input(gzipis);

		Data data = kryo.readObject(input, Data.class);
		return data;
	}

	// Tests
	public static void main(String[] args) {
		Network net = null;
		net = new Network();

		Data data = new Data();

		data.replays = new Replay[] { getBogusReplay(), getBogusReplay(),
				getBogusReplay(), getBogusReplay(), getBogusReplay(),
				getBogusReplay(), getBogusReplay(), getBogusReplay(),
				getBogusReplay(), getBogusReplay() };

		int total = 0;
		for (Replay r : data.replays)
			total += r.bytes.length;

		byte[] bytes = null;
		try {
			bytes = net.toByteArray(data);
			println("bytes: " + bytes.length + " / " + total + " ["
					+ ((float) ((float) bytes.length / (float) total))
					+ "] , replays: " + data.replays.length);
			Data data2 = net.toDataObject(bytes);
			if (data2 == null)
				println("Data2 is NULL.");
			if (data2.replays == null)
				println("Data2.Replays is NULL.");
			int sum = 0;
			for (Replay r : data2.replays)
				sum += r.bytes.length;
			println("replays: " + data2.replays.length + ", replays length: "
					+ sum);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Replay getBogusReplay() {
		Replay replay = new Replay();

		int map = (int) (30 + Math.random() * 300);
		replay.bytes = new byte[120 * map];
		for (int i = 0; i < replay.bytes.length; i++) {
			replay.bytes[i] = (byte) (i * i * i);
		}
		replay.length = replay.bytes.length;

		return replay;
	}

	public static void println(String str) {
		System.out.println(str);
	}
}