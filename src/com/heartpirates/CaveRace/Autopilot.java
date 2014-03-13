package com.heartpirates.CaveRace;

import java.awt.image.BufferedImage;

public class Autopilot extends Entity {

	int targetY;

	private final Main main;
	public int xoff = 0;
	public int yoff = 5;

	public Autopilot(Main main, int xoff, BufferedImage bimg) {
		super(bimg);
		this.xoff = xoff;
		this.main = main;
		this.targetY = main.level.height / 2;
		this.friction = 0.55;
		maxYSpeed = .9;
		minYSpeed = -.9;
	}

	int distance = 0;

	@Override
	public void tick() {
		super.tick();
		
		x = 30;

		if (main.level != null)
			this.distance = (int) (-(main.level.x));

		// next target
		int yy = targetY * main.getMapPathSize() - yoff;
		double d = Math.abs(yy - y);
		if (d > 0.5) {
			steer((double) yy - y);
		}
		
		gravity = 0;
	}

	private void steer(double d) {
		if (d < 0) {
			y--;
		}
		if (d > 0) {
			y++;
		}
	}

	public void setTarget(int target) {
//		System.out.println("Y: " + target);
		this.targetY = target;
	}
}