package com.heartpirates;

import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Audio {
	public static final HashMap<String, Clip> clipMap = getClips();

	private static HashMap<String, Clip> getClips() {
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
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	public static void play(String name) {
		try {
			Clip clip = clipMap.get(name);
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