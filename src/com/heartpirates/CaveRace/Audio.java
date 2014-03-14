package com.heartpirates.CaveRace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class Audio {
	public static final String NAME = "Audio.crdata";
	public HashMap<String, Clip> clips;
	public static Audio i = null;

	public Audio() {
		clips = Audio.getClips();
	}

	public static Audio load() {
		File file = FileSystems.getDefault().getPath(Audio.NAME).toFile();

		if (file.exists() && file.isFile()) {
			Kryo kryo = new Kryo();

			try {
				GZIPInputStream gin = new GZIPInputStream(new FileInputStream(
						file));
				Input input = new Input(gin);
				Audio a = kryo.readObject(input, Audio.class);
				input.close();
				return a;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Audio a = new Audio();
			return a;
		}

		return null;
	}

	public static void save(Audio audio) throws FileNotFoundException,
			IOException {

		synchronized (audio) {
			Kryo kryo = new Kryo();

			File file = FileSystems.getDefault().getPath(Audio.NAME).toFile();
			GZIPOutputStream gout = new GZIPOutputStream(new FileOutputStream(
					file));
			Output output = new Output(gout);
			kryo.writeObject(output, audio);
			output.close();
			System.out.println("Saved Audio.");
		}
	}

	private static HashMap<String, Clip> getClips() {
		System.out.println("Loading Sounds.");

		HashMap<String, Clip> map = new HashMap<String, Clip>();
		AudioInputStream ais = null;
		Clip clip = null;

		try {
			ais = AudioSystem.getAudioInputStream(Audio.class.getClassLoader()
					.getResource("snd/Blip1.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			map.put("Blip1", clip);

			ais = AudioSystem.getAudioInputStream(Audio.class.getClassLoader()
					.getResource("snd/Explosion1.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			map.put("Explosion1", clip);

			ais = AudioSystem.getAudioInputStream(Audio.class.getClassLoader()
					.getResource("snd/Explosion2.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			map.put("Explosion2", clip);

			ais = AudioSystem.getAudioInputStream(Audio.class.getClassLoader()
					.getResource("snd/Hurt1.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			map.put("Hurt1", clip);

			ais = AudioSystem.getAudioInputStream(Audio.class.getClassLoader()
					.getResource("snd/Spawn1.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			map.put("Spawn1", clip);

			ais = AudioSystem.getAudioInputStream(Audio.class.getClassLoader()
					.getResource("snd/Disconnect.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			map.put("Disconnect", clip);

			ais = AudioSystem.getAudioInputStream(Audio.class.getClassLoader()
					.getResource("snd/EMP.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			map.put("EMP", clip);

			ais = AudioSystem.getAudioInputStream(Audio.class.getClassLoader()
					.getResource("snd/EngineStart.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			map.put("EngineStart", clip);

			ais = AudioSystem.getAudioInputStream(Audio.class.getClassLoader()
					.getResource("snd/Intro.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			map.put("Intro", clip);

			ais = AudioSystem.getAudioInputStream(Audio.class.getClassLoader()
					.getResource("snd/Thunder.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			map.put("Thunder", clip);

			ais = AudioSystem.getAudioInputStream(Audio.class.getClassLoader()
					.getResource("snd/Waveshot.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			map.put("Waveshot", clip);

			ais = AudioSystem.getAudioInputStream(Audio.class.getClassLoader()
					.getResource("snd/Start.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			map.put("Start", clip);

			ais = AudioSystem.getAudioInputStream(Audio.class.getClassLoader()
					.getResource("snd/Start2.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			map.put("Start2", clip);

			ais = AudioSystem.getAudioInputStream(Audio.class.getClassLoader()
					.getResource("snd/Signal.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			map.put("Signal", clip);

			ais = AudioSystem.getAudioInputStream(Audio.class.getClassLoader()
					.getResource("snd/Error.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			map.put("Error", clip);

			ais = AudioSystem.getAudioInputStream(Audio.class.getClassLoader()
					.getResource("snd/Close.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			map.put("Close", clip);

			ais = AudioSystem.getAudioInputStream(Audio.class.getClassLoader()
					.getResource("snd/Error2.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			map.put("Error2", clip);

			ais = AudioSystem.getAudioInputStream(Audio.class.getClassLoader()
					.getResource("snd/Open.wav"));
			clip = AudioSystem.getClip();
			clip.open(ais);
			map.put("Open", clip);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Sounds Loaded.");
		return map;
	}

	public static void play(String name) {
		if (Audio.i == null) {
			Audio.i = load();
			try {
				Audio.save(Audio.i);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			Clip clip = Audio.i.clips.get(name);
			if (clip == null)
				return;

			if (!clip.isActive()) {
				setVolume(.6f, clip);
				clip.setFramePosition(0);
				clip.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setVolume(float percent, Clip clip) {
		FloatControl fc = (FloatControl) clip
				.getControl(FloatControl.Type.MASTER_GAIN);

		float delta = fc.getMaximum() - fc.getMinimum();
		float f = (float) (fc.getMinimum() + (delta * percent));

		if (f < fc.getMinimum())
			f = fc.getMinimum();
		if (f > fc.getMaximum())
			f = fc.getMaximum();

		fc.setValue(f);
	}
}