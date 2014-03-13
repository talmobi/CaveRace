package com.heartpirates.CaveRace.screens;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

import com.heartpirates.CaveRace.Audio;
import com.heartpirates.CaveRace.Main;

public class GameOverScreen extends TitleScreen {

	public GameOverScreen(Main main, int w, int h) {
		super(main, w, h);
	}

	enum Sel {
		RESTART, MENU
	}

	Sel sel = Sel.RESTART;

	long now = System.currentTimeMillis();
	int pressDelay = 250;

	@Override
	protected void drawfg(Graphics g) {
		if (System.currentTimeMillis() - now > pressDelay)
			updateKeyboard();

		g.setColor(txtColor);
		g.setFont(main.TITLE_FONT);
		g.drawString("Game Over", w / 2 - 37 - 17, h / 2 - 14 + pos + 4);
		g.setFont(main.FONT);

		for (int i = 0; i < sel.values().length; i++) {
			if (i == sel.ordinal()) {
				g.drawString(">" + sel.values()[i].toString(), w / 2, h / 2 + i
						* 10);
			} else
				g.drawString(" " + sel.values()[i].toString(), w / 2, h / 2 + i
						* 10);
		}
	}

	private void updateKeyboard() {
		if (main.keyboard.keys[KeyEvent.VK_0]) {
			now = System.currentTimeMillis();
			pos = -20;
		}
		if (main.keyboard.keys[KeyEvent.VK_ENTER]) {
			now = System.currentTimeMillis();
			switch (sel) {
			case MENU:
				Main.gameState = Main.State.MENU;
				Audio.play("Blip1");
				break;
			case RESTART:
				Main.gameState = Main.State.RESTART;
				Audio.play("Start2");
				break;
			}
		}
		if (main.keyboard.keys[KeyEvent.VK_DOWN]) {
			now = System.currentTimeMillis();
			Audio.play("Blip1");
			Sel[] vals = sel.values();
			int n = sel.ordinal() + 1;
			while (n >= vals.length)
				n -= vals.length;
			sel = vals[n];
		}
		if (main.keyboard.keys[KeyEvent.VK_UP]) {
			Audio.play("Blip1");
			now = System.currentTimeMillis();
			Sel[] vals = sel.values();
			int n = sel.ordinal() - 1;
			while (n < 0)
				n += vals.length;
			sel = vals[n];
		}
	}

}
