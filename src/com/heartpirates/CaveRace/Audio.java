package com.heartpirates.CaveRace;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class Audio {
	public static final String NAME = "Audio.crdata";
	public static HashMap<String, Clip> clipMap;

	public HashMap<String, Sound> clips;
	public static Audio i = null;

	public Audio() {
		clips = loadSoundsFromFileSystem();
	}

	public static void init() {
		if (Audio.i == null) {
			Audio audio = loadAudio();
			Audio.i = audio;
			Audio.clipMap = audio.loadClips();
		}
	}

	public static Audio loadAudio() {
		File file = FileSystems.getDefault().getPath(Audio.NAME).toFile();

		Kryo kryo = new Kryo();
		if (file.exists() && file.isFile()) {

			try {
				GZIPInputStream gin = new GZIPInputStream(new FileInputStream(
						file));
				Input input = new Input(gin);
				Audio kryoAudio = kryo.readObject(input, Audio.class);
				input.close();
				System.out.println("Loaded from Kryo.");
				return kryoAudio;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// try loading from within jar
			try {
				InputStream is = Audio.class.getClassLoader()
						.getResourceAsStream(Audio.NAME);
				GZIPInputStream gin = new GZIPInputStream(is);
				Input input = new Input(gin);
				Audio kryoAudio = kryo.readObject(input, Audio.class);
				input.close();
				System.out.println("Loaded from Kryo.");
				return kryoAudio;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		Audio audio = new Audio(); // load audio from filesystem
		
		// save into data object
		try {
			System.out.println("Saving Audio.");
			Audio.save(audio);
			System.out.println("Audio Saved!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return audio;
	}

	private static byte[] loadSoundBytes(String name)
			throws UnsupportedAudioFileException, IOException {
		byte[] bytes;
		int data;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		AudioInputStream ais = AudioSystem.getAudioInputStream(Audio.class
				.getClassLoader().getResource("snd/" + name + ".wav"));
		while ((data = ais.read()) >= 0) {
			buffer.write(data);
		}
		bytes = buffer.toByteArray();
		return bytes;
	}

	private static Sound loadSound(String name)
			throws UnsupportedAudioFileException, IOException {
		byte[] bytes;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// Using AudioInputStream loses significant audio info from the file.
		// AudioInputStream ais = AudioSystem.getAudioInputStream(Audio.class
		// .getClassLoader().getResource("snd/" + name + ".wav"));

		// save the pure bytes, use AudioInputStream later
		// when turning the Sound objects into Clips (loadClips())
		InputStream is = Audio.class.getClassLoader()
				.getResource("snd/" + name + ".wav").openStream();

		byte[] buffer = new byte[1024];
		int bytesRead = 0;

		while ((bytesRead = is.read(buffer)) > 0) {
			baos.write(buffer, 0, bytesRead);
		}
		baos.flush();
		bytes = baos.toByteArray();

		return new Sound(name, bytes);
	}

	private HashMap<String, Clip> loadClips() {
		HashMap<String, Clip> map = new HashMap<String, Clip>();

		// load audio clips from Sound objects
		for (Sound s : this.clips.values()) {
			Clip clip = null;
			try {
				byte[] bytes = s.bytes;
				ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				// open as AudioInputStream
				AudioInputStream ais = AudioSystem.getAudioInputStream(bais);
				clip = AudioSystem.getClip();
				clip.open(ais);
				map.put(s.name, clip);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return map;
	}

	public static HashMap<String, Sound> loadSoundsFromFileSystem() {
		HashMap<String, Sound> map = new HashMap<String, Sound>();

		try {
			map.put("Blip1", loadSound("Blip1"));
			map.put("Explosion1", loadSound("Explosion1"));
			map.put("Explosion2", loadSound("Explosion2"));
			map.put("Hurt1", loadSound("Hurt1"));
			map.put("Spawn1", loadSound("Spawn1"));
			map.put("Disconnect", loadSound("Disconnect"));
			map.put("EMP", loadSound("EMP"));
			map.put("EngineStart", loadSound("EngineStart"));
			map.put("Intro", loadSound("Intro"));
			map.put("Thunder", loadSound("Thunder"));
			map.put("Waveshot", loadSound("Waveshot"));
			map.put("Start", loadSound("Start"));
			map.put("Start2", loadSound("Start2"));
			map.put("Signal", loadSound("Signal"));
			map.put("Error", loadSound("Error"));
			map.put("Close", loadSound("Close"));
			map.put("Error2", loadSound("Error2"));
			map.put("Open", loadSound("Open"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
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
			Audio.init();
		}

		if (Audio.clipMap == null)
			return;

		try {
			Clip clip = Audio.clipMap.get(name);
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