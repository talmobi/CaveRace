package com.heartpirates;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Audio {
	public static final Clip[] clips = getClips();

	private static Clip[] getClips() {
		Clip clips[] = new Clip[5];
		try {
			AudioInputStream ais = null;
			for (int i = 0; i < 5; i++) {
				switch (i) {
				case 0:
					ais = AudioSystem.getAudioInputStream(Audio.class
							.getClassLoader().getResource("snd/Blip1.wav"));
					break;
				case 1:
					ais = AudioSystem
							.getAudioInputStream(Audio.class.getClassLoader()
									.getResource("snd/Explosion1.wav"));
					break;
				case 2:
					ais = AudioSystem
							.getAudioInputStream(Audio.class.getClassLoader()
									.getResource("snd/Explosion2.wav"));
					break;
				case 3:
					ais = AudioSystem.getAudioInputStream(Audio.class
							.getClassLoader().getResource("snd/Hurt1.wav"));
					break;
				case 4:
					ais = AudioSystem.getAudioInputStream(Audio.class
							.getClassLoader().getResource("snd/Spawn1.wav"));
					break;
				}

				Clip clip = AudioSystem.getClip();
				clip.open(ais);
				clips[i] = clip;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return clips;
	}

	public static void play(int n) {
		try {
			if (!clips[n].isActive()) {
				// FloatControl fc = (FloatControl)
				// clips[n].getControl(FloatControl.Type.MASTER_GAIN);
				// fc.setValue(-10f);
				setVolume(.6f, clips[n]);
				clips[n].setFramePosition(0);
				clips[n].start();
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