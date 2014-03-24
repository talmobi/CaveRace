package com.heartpirates.CaveRace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class AppData {

	static boolean successfullyLoaded = false;

	public String password;
	public int id;

	public String playerName = "Anon";
	public int shipNum = 0;
	public int ghostMode = 0;

	// World id, Score / Distance
	// HashMap<Integer, Integer> levelMap = new HashMap<Integer, Integer>();

	List<Replay> topReplays = new LinkedList<Replay>();
	List<Replay> tempReplays = new LinkedList<Replay>();

	public AppData() {
	}

	public static void save(AppData appData) throws FileNotFoundException,
			IOException {
		if (!successfullyLoaded)
			throw new IOException("Can't save. File loaded unsuccessfully.");

		synchronized (appData) {
			Kryo kryo = new Kryo();

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

		File file = FileSystems.getDefault().getPath("AppData").toFile();
		GZIPInputStream gin = new GZIPInputStream(new FileInputStream(file));
		Input input = new Input(gin);

		AppData a = kryo.readObject(input, AppData.class);
		input.close();

		System.out.println("AppData Loaded. replays: " + a.tempReplays.size());

		successfullyLoaded = true;
		return a;
	}

	public Replay getReplay(int index) {
		if (tempReplays.isEmpty() || index >= tempReplays.size() || index < 0)
			return null;
		return tempReplays.get(index);
	}

	public Replay getReplay() {
		return getReplay(0);
	}

	public void addReplay(Replay replay) {
		tempReplays.add(0, replay);

		// CaveRace.sort(tempReplays);

		while (tempReplays.size() > 100)
			tempReplays.remove(tempReplays.size() - 1);
	}

	public static void save(final AppData appData, final String string) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (!successfullyLoaded) {
					System.out
							.println("Failed to load AppData - Save prevented.");
					return;
				}

				try {
					synchronized (appData) {
						Kryo kryo = new Kryo();

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
			if (!tempRep.name.equalsIgnoreCase(playerName))
				continue;
			int tempScore = tempRep.bytes.length;
			if (tempScore > score) {
				score = tempScore;
				rep = tempRep;
			}
		}

		if (rep != null)
			System.out.println("Retuing replay: " + rep.name + ", score: "
					+ rep.bytes.length);
		return rep;
	}

	public int getSize() {
		return this.tempReplays.size();
	}

	public Replay getWorldHighScoreReplay(int world) {
		if (topReplays == null || topReplays.isEmpty())
			return getHighScoreReplay(world);

		int score = 0;
		Replay rep = null;
		for (int i = 0; i < topReplays.size(); i++) {
			Replay tempRep = topReplays.get(i);
			int tempScore = tempRep.length;
			if (tempScore > score) {
				score = tempScore;
				rep = tempRep;
			}
		}

		return rep;
	}
}