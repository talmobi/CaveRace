package com.heartpirates.CaveRace;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Ghostpilot extends Autopilot {

	public Ghostpilot(Main main, int xoff, BufferedImage bimg) {
		super(main, xoff, bimg);
	}

	int ticks = 0;
	int blink = 6;
	boolean visible = true;
	boolean fade = false;
	int ticksDead = 0;

	@Override
	public void tick() {
		ticks++;
		if (fade)
			ticksDead++;
		gravity = 0;

		if (ticksDead > 6 * 20) {
			this.remove = true;
		}
	}

	@Override
	public void render(Graphics g) {
		if (ticks > blink && fade) {
			ticks = 0;
			visible = !visible;
		}

		if (visible)
			super.render(g);
	}

	public void fadeOut() {
		if (!this.fade) {
			Audio.play("Explosion2");
			Audio.play("Signal");
			this.fade = true;
		}
	}

}