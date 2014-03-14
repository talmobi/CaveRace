package com.heartpirates.CaveRace;

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

	Recorder rec;

	public Player(Main main, BufferedImage bimg) {
		super(bimg);
		this.main = main;
		rec = new Recorder(main.level.seed, main.level.world);
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
		if (_ticks > _blink && _fade) {
			_ticks = 0;
			_visible = !_visible;
		}

		if (_visible)
			super.render(g);
	}

	int _ticks = 0;
	int _blink = 6;
	boolean _visible = true;
	boolean _fade = false;
	int _ticksDead = 0;

	@Override
	public void tick() {
		updateKeyboard();
		super.tick();

		if (main.level.isBlocked((int) (x + 2), (int) (y + 5))) {
			collide();
		}

		gravity = 0;

		_ticks++;
		if (_fade)
			_ticksDead++;

		if (_ticksDead > 6 * 20 && !remove) {
			this.remove = true;
			onRemove();
		}
	}

	public void record() {
		// record
		if (!remove) {
			rec.add((int) y);
		}
	}

	private void onRemove() {
	}

	public void fadeOut() {
		this._fade = true;
		Jeeves.i.radio.loadAndPlay("mus/Falex.sap");
		Audio.play("Disconnect");
	}

	@Override
	public void collide() {
		if (!_fade) {
			Audio.play("Thunder");
			fadeOut();
		}
	}

	public void updateKeyboard() {
		Keyboard kb = main.keyboard;

		if (_fade)
			return;

		if (kb.keys[KeyEvent.VK_Q]) {
			yspeed -= 0.04;
			lastPressed = System.currentTimeMillis();
		}
		if (kb.keys[KeyEvent.VK_W]) {
			yspeed -= 0.08;
			lastPressed = System.currentTimeMillis();
		}
		if (kb.keys[KeyEvent.VK_A]) {
			yspeed += 0.04;
			lastPressed = System.currentTimeMillis();
		}
		if (kb.keys[KeyEvent.VK_S]) {
			yspeed += 0.08;
			lastPressed = System.currentTimeMillis();
		}
	}

	public void reset() {
		// Player copy = new Player(main);
		// this.x = copy.x;
		// this.y = copy.y;
		x = 0;
		y = 10;
		this.remove = false;
		this._fade = false;
		this._visible = true;
		this._ticks = 0;
		this._ticksDead = 0;
		this.rec = new Recorder(main.level.seed, main.level.world);
	}

}