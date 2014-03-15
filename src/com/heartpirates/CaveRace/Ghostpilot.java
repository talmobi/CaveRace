package com.heartpirates.CaveRace;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Ghostpilot extends Autopilot {

	public Ghostpilot(CaveRace main, int xoff, BufferedImage bimg) {
		super(main, xoff, bimg);
	}

	int ticks = 0;
	int blink = 3;
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
			this.visible = false;
			ticks = 0;
			Audio.play("Signal");
			this.fade = true;
		}
	}

	public void reset() {
		x = 20;
		y = 10;
		this.remove = false;
		this.fade = false;
		this.visible = true;
		this.ticks = 0;
		this.ticksDead = 0;
	}

}