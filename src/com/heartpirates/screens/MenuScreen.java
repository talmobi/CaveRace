package com.heartpirates.screens;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import com.heartpirates.Audio;
import com.heartpirates.Jeeves;
import com.heartpirates.Main;
import com.heartpirates.Sprite;

public class MenuScreen extends Screen {

	int ship = 0;
	int maxShips = 6;

	enum Selection {
		START, REPLAYS, SCORE, SHIP
	}

	Selection sel = Selection.START;
	Selection lastSel = sel;

	public MenuScreen(Main main, int w, int h) {
		super(main, w, h);
	}

	long now = System.currentTimeMillis();
	int pressDelay = 250;

	int LEFT = KeyEvent.VK_LEFT;
	int RIGHT = KeyEvent.VK_RIGHT;
	int UP = KeyEvent.VK_UP;
	int DOWN = KeyEvent.VK_DOWN;
	int ENTER = KeyEvent.VK_ENTER;

	private List<Sprite> ships = new LinkedList<Sprite>();

	private class Circler extends Sprite {
		double r = 11;
		double dir = -90;
		double newDir = dir;

		public Circler(BufferedImage bimg) {
			super(bimg);
		}

		public void reset() {
			r = 11;
			dir = -90;
			newDir = dir;
		}

		@Override
		public void tick() {
			while (dir >= 360)
				dir -= 360;
			while (dir < 0)
				dir += 360;

			for (int i = 0; i < 4; i++) {
				int d = (int) ((newDir - dir + 360 * 2) % 360);
				if (d > 180) {
					dir++;
				}
				if (d < 180) {
					dir--;
				}
			}
		}

		@Override
		public void render(Graphics g) {
			double rads = Math.toRadians(dir);
			int x = (int) (Math.cos(rads) * r);
			int y = (int) (Math.sin(rads) * r);

			g.drawImage(this.image, (int) x + 20, (int) y + 30 + 12, this.w,
					this.h, null);
		}
	}

	@Override
	protected void drawfg(Graphics g) {
		if (ships.isEmpty()) {
			for (int i = 0; i < maxShips; i++) {
				Circler c = new Circler(Jeeves.i.ships[i][0]);
				c.newDir += (i * 60 - 90);
				ships.add(c);
			}
		}

		// draw selections
		for (int i = 0; i < 4; i++) {
			int y = 10 + i * 9 + 6;
			int x = w / 2 + 9;
			String str = Selection.values()[i].toString();
			if (sel.ordinal() == i) {
				g.drawString(">", x - 12, y);
				g.drawString("  " + str, x - 18, y);
			} else {
				g.drawString(str, x, y);
			}
		}

		// draw ships
		if (sel == Selection.SHIP) {
			// reset ships
			if (lastSel != sel) {
				for (Sprite s : ships) {
					((Circler) s).reset();
				}
			}

			for (int i = 0; i < maxShips; i++) {
				Sprite s = ships.get(i);
				s.render(g);
				s.tick();

				// set ship direction
				Circler c = (Circler) s;
				c.newDir = ((i - ship) * 60) + 30 + 60;

				// render selected ship (BIG)
				if (i == ship) {
					c.r = 14;
					BufferedImage img = s.image;
					g.drawImage(img, 16 - 4 + 2, 0, img.getWidth() * 3,
							img.getHeight() * 3, null);
				} else {
					c.r = 11;
				}
			}

		}

		lastSel = sel;
	}
	
	public void playBlip() {
		Audio.play("Blip1");
	}

	private void drawSelection(Graphics g) {
		int count = 0;
		for (int i = 0; i < maxShips; i++) {
			int n = (i + ship) % maxShips;
			BufferedImage img = Jeeves.i.ships[n][0];
			int y = 52;
			int x = 20;
			if (ship != n) {
				count++;
				// int x = count * img.getWidth() + 1 + 10;
				int xx = x;
				int yy = y;

				switch (count) {
				case 1:
					xx += 10;
					yy -= 16;
					break;
				case 2:
					xx += 8;
					yy -= 8;
					break;
				case 3:
					xx += 0;
					yy -= 2;
					break;
				case 4:
					xx -= 8;
					yy -= 8;
					break;
				case 5:
					xx -= 10;
					yy -= 16;
					break;
				}

				g.drawImage(img, xx, yy, img.getWidth(), img.getHeight(), null);
			} else {
				// big igm
				g.drawImage(img, 16 - 2, 14 - 2, img.getWidth() * 3,
						img.getHeight() * 3, null);
			}
		}
	}

	@Override
	public void tick() {
		if (!(System.currentTimeMillis() - now > pressDelay))
			return;
		boolean[] keys = main.keyboard.keys;
		if (keys[ENTER]) {
			now = System.currentTimeMillis();
			Audio.play("Start2");
			main.gameState = Main.State.RESTART;
		}
		
		if (keys[RIGHT]) {
			playBlip();
			now = System.currentTimeMillis();
			if (sel == Selection.SHIP) {
				ship--;
				while (ship < 0)
					ship += maxShips;
				System.out.println("ship: " + ship);
			}
		}

		if (keys[LEFT]) {
			playBlip();
			now = System.currentTimeMillis();
			if (sel == Selection.SHIP) {
				ship++;
				while (ship >= maxShips)
					ship -= maxShips;
				System.out.println("ship: " + ship);
			}
		}

		if (keys[UP]) {
			playBlip();
			now = System.currentTimeMillis();
			int n = sel.ordinal() - 1;
			Selection[] vals = Selection.values();
			while (n < 0)
				n += vals.length;
			sel = vals[n];
		}

		if (keys[DOWN]) {
			playBlip();
			now = System.currentTimeMillis();
			int n = sel.ordinal() + 1;
			Selection[] vals = Selection.values();
			while (n >= vals.length)
				n -= vals.length;
			sel = vals[n];
		}
	}
	
	@Override
	public void onSwitch() {
		now = System.currentTimeMillis() + 200;
	}

}