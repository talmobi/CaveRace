package com.heartpirates.CaveRace;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	public HashMap<String, Sound> map;

	public static MusicData load() throws IOException {
		File file = new File(FILENAME);
		MusicData md = null;

		if (file.exists() && file.isFile() && false) {
			try {
				GZIPInputStream gin = new GZIPInputStream(new FileInputStream(
						file));
				Input input = new Input(gin);
				Kryo kryo = new Kryo();
				md = kryo.readObject(input, MusicData.class);
				input.close();
				System.out.println("---MUSIC LOADED FROM FILE---");
			} catch (Exception e) {
				e.printStackTrace();
				md = null;
			}
		} else {
			// try reading from within jar
			try {
				InputStream is = MusicData.class.getClassLoader()
						.getResourceAsStream(FILENAME);
				GZIPInputStream gin = new GZIPInputStream(is);
				Input input = new Input(gin);
				Kryo kryo = new Kryo();
				md = kryo.readObject(input, MusicData.class);
				input.close();
				System.out.println("---MUSIC LOADED FROM URL---");
			} catch (Exception e) {
				e.printStackTrace();
				md = null;
			}
		}

		if (md == null) {
			System.out.println("<<< MUSIC RECREATED >>>");
			md = new MusicData();
			md.init(); // load all music files

			// try {
			// Thread.sleep(100);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			//
			// // write it to file
			// FileOutputStream fos = new FileOutputStream(file);
			// Output output = new Output(fos);
			// Kryo kryo = new Kryo();
			// kryo.writeObject(output, md);
			// output.close();
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
		map = new HashMap<String, Sound>();

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
			while ((b = is.read()) != -1) {
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

		// self save
		save();
	}

	private void save() throws IOException {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// write it to file
		FileOutputStream fos = new FileOutputStream(new File(FILENAME));
		GZIPOutputStream gos = new GZIPOutputStream(fos);
		Output output = new Output(gos);
		Kryo kryo = new Kryo();
		kryo.writeObject(output, this);
		output.close();
	}

	public byte[] getMusicBytes(String name) {
		Sound sound = map.get(name);
		if (sound == null)
			return null;
		return sound.bytes;
	}

}