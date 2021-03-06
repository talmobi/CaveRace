package com.heartpirates.CaveRace;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import net.sf.asap.ASAP;
import net.sf.asap.ASAPInfo;

// plays music
public class Radio implements Runnable {
	private final ASAP asap = new ASAP();
	private SourceDataLine line;
	private int song = 0;

	private AtomicBoolean playing = new AtomicBoolean(false);
	private float floatVolume = 0.40f;
	private float defaultVolume = 0.8f;
	private final Thread t = new Thread(this);

	private String nowPlaying = "";

	private final Object lock = new Object();
	
	private final MusicData md;
	
	public Radio() throws IOException {
		md = MusicData.load();
	}

	@Override
	public void run() {
		byte[] bytes = new byte[1024 << 3];

		while (true) {

			synchronized (lock) {
				if (playing.get()) {
					int d;
					d = this.asap.generate(bytes, bytes.length, 1);
					this.line.write(bytes, 0, d);

					if (d != bytes.length) {
						playing.set(false);
					}
				}
			}

			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void stopMusic() {
		synchronized (lock) {
			if (playing.get()) {
				playing.set(false);
				this.line.stop();
				System.out.println("Music stopped.");
			}
		}
	}

	void loadMusic(String name) {
		synchronized (lock) {
			System.out.println("Loading sounds...");
			byte[] bytes;
			int length;

			bytes = md.getMusicBytes(name.substring(4));
			
			if (bytes == null) {
				System.out.println("Failed to load music: " + name);
				return;
			}
			
			length = bytes.length;
			
			loadAsapMusic(name, bytes, length);

			setVolume(defaultVolume);
		}
	}

	void loadAsapMusic(String name, byte[] bytes, int length) {
		ASAPInfo info;
		try {
			this.asap.load(name, bytes, length);
			info = this.asap.getInfo();
			this.asap.playSong(song,
					info.getLoop(song) ? -1 : info.getDuration(song));
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

		AudioFormat af = new AudioFormat(44100.0F, 16, info.getChannels(),
				true, false);
		try {
			this.line = ((SourceDataLine) AudioSystem
					.getLine(new DataLine.Info(SourceDataLine.class,
							(AudioFormat) af)));
			this.line.open((AudioFormat) af);
			setVolume(floatVolume);
		} catch (LineUnavailableException lue) {
			lue.printStackTrace();
			return;
		}
	}

	public void playMusic() {
		synchronized (lock) {
			if (!playing.get()) {
				this.playing.set(true);
				this.line.start();
				System.out.println("Playing Music.");
				if (!t.isAlive()) {
					t.start();
				}
			}
		}
	}

	private int readAndClose(InputStream is, byte[] bytes) throws IOException {
		int index = 0;
		int maxLength = bytes.length;
		try {
			while (index < maxLength) {
				int bytesRead = is.read(bytes, index, maxLength - index);
				if (bytesRead <= 0)
					break;
				index += bytesRead;
			}
		} finally {
			is.close();
		}
		return index;
	}

	public void setVolume(float percent) {
		FloatControl fc = (FloatControl) this.line
				.getControl(FloatControl.Type.MASTER_GAIN);

		float delta = fc.getMaximum() - fc.getMinimum();
		float f = (float) (fc.getMaximum() - delta * (1 - percent));

		if (f < fc.getMinimum())
			f = fc.getMinimum();
		if (f > fc.getMaximum())
			f = fc.getMaximum();

		floatVolume = f;
		fc.setValue(f);
		System.out.println("Volume: " + floatVolume);
	}

	public void setVolume(int i) {
		FloatControl fc = (FloatControl) this.line
				.getControl(FloatControl.Type.MASTER_GAIN);
		System.out.println("Volume: " + floatVolume);

		float pc = fc.getPrecision();

		float f = floatVolume;

		if (i < 0)
			f -= pc;
		if (i > 0)
			f += pc;

		if (f < fc.getMinimum())
			f = fc.getMinimum();
		if (f > fc.getMaximum())
			f = fc.getMaximum();

		this.floatVolume = f;
		fc.setValue(floatVolume);
	}

	public boolean isPlaying() {
		return this.line.isActive();
	}

	public void loadAndPlay(String name) {
		if (name.equalsIgnoreCase(nowPlaying)) {
			System.out.println("Song already playing.");
			return;
		}
		
		try {
			stopMusic();
			Thread.sleep(20);
			loadMusic(name);
			Thread.sleep(20);
			nowPlaying = name;
			playMusic();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getNowPlaying() {
		return nowPlaying;
	}
}