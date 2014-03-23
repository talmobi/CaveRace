package com.heartpirates.restful;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.heartpirates.CaveRace.Replay;
import com.heartpirates.CaveRace.net.Data;
import com.heartpirates.CaveRace.net.Network;

/**
 * Program to load save and handle highscores RESTfully.
 * 
 * @author hannyajin
 * 
 */
public class Restful {

	public final String DIR = "CaveRaceHighscores";
	public static final String NAME = ".caveTopScoresData.crdata";

	private ArrayList<Replay> list = new ArrayList<Replay>();

	private boolean verbose = true;

	public void init(String[] args) {

		Kryo kryo = new Kryo();
		boolean shouldSave = false;
		
		if (args[0].equals("CLEAN")) {
			
			ArrayList<Replay> clean = new ArrayList<Replay>();
			
			for (Replay r : list) {
				boolean shouldAdd = true;
				for (Replay r2 : clean) {
					if (r.equals(r2)) {
						shouldAdd =false;
						break;
					}
				}
				if (shouldAdd)
					clean.add(r);
			}
			
			if (clean.size() != list.size()) {
				this.list = clean;
				shouldSave = true;
			}
		} else

		if (args[0].equals("POST")) {
			int size = Integer.parseInt(args[1]);
			byte[] bytes = new byte[size];
			try {
				InputStream in = System.in;
				in.read(bytes);
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			Network net;
			try {
				net = new Network();
				Data data = net.toDataObject(bytes);
				Replay replay = data.replays[0];
				shouldSave = addReplay(replay);

				System.out.println("Replays: " + data.replays.length);
				System.out.println("Name: " + replay.name + ", Ship: "
						+ replay.ship + ", Length: " + replay.bytes.length);
				System.out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else

		if (args[0].equals("TOP")) {
			int len = Math.min(10, list.size());
			Replay[] reps = new Replay[len];
			for (int i = 0; i < len; i++) {
				reps[i] = list.get(i); // get top 10 replays
			}
			Data data = new Data();
			data.replays = reps;
			data.id = Data.ID.GETTOP;
			Network net = new Network();
			try {
				byte[] bytes = net.toByteArray(data);
				System.out.print("Length: " + bytes.length + "\r\n");
				System.out.write(bytes);
				System.out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (shouldSave) {
			try {
				File file = new File(NAME);
				Output output = new Output(new FileOutputStream(file));
				kryo.writeObject(output, this);
				System.out.println("saved");
				output.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param replay
	 * @return true if list has been updated
	 */
	private boolean addReplay(Replay replay) {
		for (int i = 0; i < list.size(); i++) {
			Replay r = list.get(i);
			if (r.equals(replay))
				return false;
			if (r.bytes.length < replay.bytes.length) {
				list.add(i, replay);
				return true;
			}
		}

		if (list.size() < 10) {
			list.add(replay);
			return true;
		}

		return false;
	}

	public static void main(String[] args) {
		Kryo kryo = new Kryo();
		File file = new File(NAME);
		Restful rest = null;
		try {
			Input input = new Input(new FileInputStream(file));
			rest = kryo.readObject(input, Restful.class);
			input.close();
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}

		if (rest == null) {
			rest = new Restful();
		}

		rest.init(args);
	}

	public void print(String str) {
		if (!verbose)
			return;
		System.out.println(str);
	}
}