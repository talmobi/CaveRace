package com.heartpirates;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class AppData {

	String name;
	String password;
	int id;

	// World id, Score / Distance
	HashMap<Integer, Integer> levelMap = new HashMap<Integer, Integer>();

	public AppData() {
	}

	public static void save(AppData appData) throws FileNotFoundException,
			IOException {
		Kryo kryo = new Kryo();
		kryo.register(AppData.class);

		File file = FileSystems.getDefault().getPath("AppData").toFile();
		GZIPOutputStream gout = new GZIPOutputStream(new FileOutputStream(file));
		Output output = new Output(gout);
		kryo.writeObject(output, appData);
		output.close();
	}

	public static AppData load() throws FileNotFoundException, IOException {
		Kryo kryo = new Kryo();
		kryo.register(AppData.class);

		File file = FileSystems.getDefault().getPath("AppData").toFile();
		GZIPInputStream gin = new GZIPInputStream(new FileInputStream(file));
		Input input = new Input(gin);
		return kryo.readObject(input, AppData.class);
	}
}