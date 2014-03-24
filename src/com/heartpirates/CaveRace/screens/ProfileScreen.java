package com.heartpirates.CaveRace.screens;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

import com.heartpirates.CaveRace.Audio;
import com.heartpirates.CaveRace.CaveRace;
import com.heartpirates.CaveRace.CaveRace.State;

public class ProfileScreen extends Screen {

	public ProfileScreen(CaveRace main, int w, int h) {
		super(main, w, h);
	}

	boolean blink = false;
	public String name = "Anon";

	long now = System.currentTimeMillis();
	int pressDelay = 100;

	@Override
	public void tick() {
		super.tick();

		if (System.currentTimeMillis() - now < pressDelay)
			return;

		boolean[] keys = game.keyboard.keys;

		if (keys[KeyEvent.VK_ENTER]) {
			now = System.currentTimeMillis();
			Audio.play("Blip1");
			game.getAppData().playerName = name;
			game.setGameState(State.MENU);
			return;
		}

		if (keys[KeyEvent.VK_BACK_SPACE]) {
			now = System.currentTimeMillis();
			if (name.length() > 0)
				name = name.substring(0, name.length() - 1);
		}

		if (game.keyboard.keyTyped()) {
			now = System.currentTimeMillis();
			name += game.keyboard.lastTyped();
		}

	}

	@Override
	public void onSwitch() {
		now = System.currentTimeMillis() + 200;
		game.keyboard.keyTyped();
	}

	@Override
	protected void drawfg(Graphics g) {
		if ((System.currentTimeMillis() / 1000) % 2 == 0)
			blink = true;
		else
			blink = false;

		g.drawString("Enter your name", 7, 20);
		g.drawString("(For Leaderboards)", 1, 30);

		if (name.length() > 9)
			name = name.substring(0, 9);
		if (blink)
			g.drawString("name: " + name + "|", 13, 50);
		else
			g.drawString("name: " + name, 13, 50);
	}
}