package com.heartpirates;

import java.awt.image.BufferedImage;

public class Entity extends Sprite {

	double xspeed;
	double yspeed;

	public double friction = 0.85;
	public static final double _spd = 0.2;
	double gravity = 0.035 * _spd;
	double maxYSpeed = .5;
	double minYSpeed = -.5;

	public Entity(BufferedImage bimg) {
		super(bimg);
	}

	public void move() {
		if (yspeed < minYSpeed)
			yspeed = minYSpeed;
		if (yspeed > maxYSpeed)
			yspeed = maxYSpeed;

		this.x += xspeed;
		this.y += yspeed;

		xspeed *= friction;
		yspeed *= friction;

		yspeed += gravity;

		// TODO
		if (y > 60) {
			y = 60;
			yspeed = 0;
		}
	}

	@Override
	public void tick() {
		move();
	}

}