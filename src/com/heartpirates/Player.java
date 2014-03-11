package com.heartpirates;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Player extends Entity {

	private final Main main;

	double friction = 0.85;
	double gravity = 0.0005;
	double maxYSpeed = .55;
	double minYSpeed = -.55;
	double speed = .4;

	long lastPressed = System.currentTimeMillis();
	long gravityDelay = 800;

	public Player(Main main, BufferedImage bimg) {
		super(bimg);
		this.main = main;
	}

	public Player(Main main) {
		this(main, new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB));
	}

	@Override
	public void move() {
		long now = System.currentTimeMillis();
		if (yspeed < minYSpeed)
			yspeed = minYSpeed;
		if (yspeed > maxYSpeed)
			yspeed = maxYSpeed;

		this.x += xspeed;
		this.y += yspeed;

		if (Math.abs(yspeed) < 0.00001)
			yspeed = 0;

		if (yspeed != 0 && now - lastPressed < gravityDelay)
			yspeed *= friction;
		else
			yspeed += gravity;

		xspeed *= friction;

		// TODO
		if (y > 60) {
			y = 60;
			yspeed = 0;
		}
	}
	
	@Override
	public void render(Graphics g) {
//		super.render(g);
	}

	@Override
	public void tick() {
		updateKeyboard();
		super.tick();

		if (main.level.isBlocked((int) (x + 2), (int) (y + 5))) {
//			System.out.println("BOOOM!!");

			x = 0;
			y = main.HEIGHT / 2;
		}
	}

	public void updateKeyboard() {
		Keyboard kb = main.keyboard;

		if (kb.keys[KeyEvent.VK_Q]) {
			yspeed -= speed;
			lastPressed = System.currentTimeMillis();
		}
		if (kb.keys[KeyEvent.VK_W]) {
			yspeed -= 0.05;
			lastPressed = System.currentTimeMillis();
		}
		if (kb.keys[KeyEvent.VK_A]) {
			yspeed += speed;
			lastPressed = System.currentTimeMillis();
		}
		if (kb.keys[KeyEvent.VK_S]) {
			yspeed += 0.05;
			lastPressed = System.currentTimeMillis();
		}
	}

}