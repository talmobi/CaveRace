package com.heartpirates.CaveRace.net;

import com.heartpirates.CaveRace.Replay;

public class Data {
	public enum ID {
		TEST, PING, PONG, POST, GETTOP
	}
	public ID id;
	public Replay[] replays;
}