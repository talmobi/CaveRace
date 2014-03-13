package com.heartpirates;

import java.util.LinkedList;
import java.util.List;

public class Recorder {

	final long seed;
	final int world;
	String name = "Anon";
	int id = 0;
	List<Byte> list = new LinkedList<Byte>();

	public Recorder(long seed, int world) {
		this.seed = seed;
		this.world = world;
	}

	public void add(int i) {
		list.add((byte) i);
	}

	public int getLength() {
		return list.size();
	}

	public byte[] toBytes() {
		byte[] bytes = new byte[list.size()];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = list.get(i);
		}
		return bytes;
	}

	public Replay getReplay() {
		Replay replay = new Replay();
		
		replay.name = name;
		replay.id = id;
		replay.seed = this.seed;
		replay.length = getLength();
		replay.bytes = toBytes();
		
		return replay;
	}
}