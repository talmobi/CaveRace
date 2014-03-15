package com.heartpirates.CaveRace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class AppData {

	String password;
	int id;

	// World id, Score / Distance
	HashMap<Integer, Integer> levelMap = new HashMap<Integer, Integer>();

	List<Replay> savedReplays = new LinkedList<Replay>();
	List<Replay> tempReplays = new LinkedList<Replay>();

	public AppData() {
	}

	public static void save(AppData appData) throws FileNotFoundException,
			IOException {
		synchronized (appData) {
			Kryo kryo = new Kryo();
			// kryo.register(AppData.class);

			File file = FileSystems.getDefault().getPath("AppData").toFile();
			GZIPOutputStream gout = new GZIPOutputStream(new FileOutputStream(
					file));
			Output output = new Output(gout);
			kryo.writeObject(output, appData);
			output.close();
		}
	}

	public static AppData load() throws FileNotFoundException, IOException {
		Kryo kryo = new Kryo();
		// kryo.register(AppData.class);

		File file = FileSystems.getDefault().getPath("AppData").toFile();
		GZIPInputStream gin = new GZIPInputStream(new FileInputStream(file));
		Input input = new Input(gin);

		AppData a = kryo.readObject(input, AppData.class);

		System.out.println("AppData Loaded. replays: " + a.tempReplays.size());

		return a;
	}

	public Replay getReplay(int index) {
		if (tempReplays.isEmpty() || index >= tempReplays.size())
			return null;
		return tempReplays.get(index);
	}

	public Replay getReplay() {
		return getReplay(0);
	}

	public void addReplay(Replay replay) {
		tempReplays.add(0, replay);
		while (tempReplays.size() > 100)
			tempReplays.remove(tempReplays.size() - 1);
	}

	public static void save(final AppData appData, final String string) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized (appData) {
						Kryo kryo = new Kryo();
						// kryo.register(AppData.class);

						File file = FileSystems.getDefault().getPath(string)
								.toFile();
						GZIPOutputStream gout = new GZIPOutputStream(
								new FileOutputStream(file));
						Output output = new Output(gout);
						kryo.writeObject(output, appData);
						output.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public Replay getHighScoreReplay(int world) {
		int score = 0;
		Replay rep = null;
		for (int i = 0; i < tempReplays.size(); i++) {
			Replay tempRep = tempReplays.get(i);
			int tempScore = tempRep.length;
			if (tempScore > score) {
				score = tempScore;
				rep = tempRep;
			}
		}

		return rep;
	}
}