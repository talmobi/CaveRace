package com.heartpirates.CaveRace;


public class Replay {

	int ship = 0;
	int world = 0;
	int id;
	String name;
	long seed;
	int length;
	byte[] bytes;

	public int get(int n) {
		if (n >= length)
			return -1;
		return bytes[n];
	}
}