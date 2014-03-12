package com.heartpirates;

import java.util.LinkedList;
import java.util.List;

public class Recorder {

	final long seed;
	List<Byte> list = new LinkedList<Byte>();

	public Recorder(long seed) {
		this.seed = seed;
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
		
		replay.seed = this.seed;
		replay.length = getLength();
		replay.bytes = toBytes();
		
		return replay;
	}
}