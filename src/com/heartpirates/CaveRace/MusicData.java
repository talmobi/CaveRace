package com.heartpirates.CaveRace;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class MusicData {

	public static final String FILENAME = "Music.crdata";
	HashMap<String, Sound> map = new HashMap<String, Sound>();

	public static MusicData load() throws IOException {
		File file = new File(FILENAME);
		MusicData md = null;

		if (file.exists() && file.isFile()) {
			try {
				GZIPInputStream gos = new GZIPInputStream(new FileInputStream(
						file));
				Input input = new Input(gos);
				Kryo kryo = new Kryo();
				md = kryo.readObject(input, MusicData.class);
				input.close();
			} catch (Exception e) {
				md = null;
			}
		}

		if (md == null) {
			md = new MusicData();
			md.init(); // load all music files

			// write it to file
			GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(
					file));
			Output output = new Output(gos);
			Kryo kryo = new Kryo();
			kryo.writeObject(output, md);
			output.clear();
		}

		return md;
	}

	// TEST
	public static void main(String[] args) {
		try {
			new MusicData().init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void init() throws IOException {
		// load all music files form the directory
		String path = "res/mus";
		File folder = new File(path);

		File[] list = folder.listFiles();
		String[] files = new String[list.length];

		for (int i = 0; i < list.length; i++) {
			files[i] = list[i].getName();
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (String file : files) {
			baos.reset();
			URL url = this.getClass().getClassLoader()
					.getResource("mus/" + file);
			InputStream is = url.openStream();
			int b;
			while ((b = is.read()) >= 0) {
				baos.write(b);
			}
			baos.flush();

			byte[] bytes = baos.toByteArray();
			Sound sound = new Sound(file, bytes);
			this.map.put(file, sound);

			System.out.println(file + " saved as Sound.");
			is.close();
		}
		baos.close();
	}

	public byte[] getMusicBytes(String name) {
		Sound sound = map.get(name);
		if (sound == null)
			return null;
		return sound.bytes;
	}

}