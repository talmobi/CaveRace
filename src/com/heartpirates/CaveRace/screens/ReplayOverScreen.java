package com.heartpirates.CaveRace.screens;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

import com.heartpirates.CaveRace.Audio;
import com.heartpirates.CaveRace.CaveRace;
import com.heartpirates.CaveRace.Level;
import com.heartpirates.CaveRace.Replay;
import com.heartpirates.CaveRace.CaveRace.State;

public class ReplayOverScreen extends TitleScreen {

	public ReplayOverScreen(CaveRace main, int w, int h) {
		super(main, w, h);
	}

	enum Sel {
		RESTART, REPLAYS, MENU
	}

	Sel sel = Sel.RESTART;

	long now = System.currentTimeMillis();
	int pressDelay = 250;

	@Override
	protected void drawfg(Graphics g) {
		if (System.currentTimeMillis() - now > pressDelay)
			updateKeyboard();

		g.setColor(txtColor);
		g.setFont(game.TITLE_FONT);
		g.drawString("Replay Over", w / 2 - 37 - 17, h / 2 - 14 + pos + 4);
		g.setFont(game.FONT);

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
		if (!game.showGameOver)
			return;

		if (game.keyboard.keys[KeyEvent.VK_0]) {
			now = System.currentTimeMillis();
			pos = -20;
		}
		
		if (game.keyboard.keys[KeyEvent.VK_ENTER]) {
			now = System.currentTimeMillis();
			switch (sel) {
			case RESTART:
				Level newLevel = new Level(game, CaveRace.WIDTH, CaveRace.HEIGHT, game.replay.seed);
				game.level = newLevel;
				game.setGameState(State.PLAY_REPLAY);
				Audio.play("Blip1");
				game.prepareReplay();
				break;
			case REPLAYS:
				game.setGameState(State.MENU_REPLAY);
				Audio.play("Blip1");
				break;
			case MENU:
				game.setGameState(State.MENU);
				Audio.play("Blip1");
				break;
			}
			return;
		}
		
		if (game.keyboard.keys[KeyEvent.VK_DOWN]) {
			now = System.currentTimeMillis();
			Audio.play("Blip1");
			Sel[] vals = sel.values();
			int n = sel.ordinal() + 1;
			while (n >= vals.length)
				n -= vals.length;
			sel = vals[n];
		}
		if (game.keyboard.keys[KeyEvent.VK_UP]) {
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