package com.heartpirates.CaveRace;

public class Replay {

	public int ship = 0;
	public int world = 0;
	public int id;
	public String name;
	public long seed;
	public int length;
	public byte[] bytes;

	public int get(int n) {
		if (n >= length)
			return -1;
		return bytes[n];
	}
}