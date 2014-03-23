package com.heartpirates.CaveRace;

public class Replay {

	public int ship = 0;
	public int world = 0;
	public int id;
	public String name;
	public long seed;
	public int length;
	public byte[] bytes;
	public long time;

	public int get(int n) {
		if (n >= bytes.length)
			return -1;
		return bytes[n];
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Replay) {
			Replay o = (Replay) obj;
			return length == o.length && time == o.time && name.equalsIgnoreCase(o.name) && id == o.id && world == o.world && seed == o.seed && ship == o.ship;
		}
		return super.equals(obj);
	}
}