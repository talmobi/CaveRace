package com.heartpirates;

import java.awt.image.BufferedImage;

public class Ghostpilot extends Autopilot {

	public Ghostpilot(Main main, int xoff, BufferedImage bimg) {
		super(main, xoff, bimg);
	}

	@Override
	public void tick() {
		gravity = 0;
	}

}