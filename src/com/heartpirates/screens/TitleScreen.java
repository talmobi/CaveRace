package com.heartpirates.screens;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

import com.heartpirates.Main;

public class TitleScreen extends Screen {

	int ticks = -20;
	int tickl = 2;
	int pos = -20;
	
	public TitleScreen(Main main, int w, int h) {
		super(main, w, h);
	}
	
	@Override
	public void onSwitch() {
		pos = -20;
		ticks = 0;
	}
	
	@Override
	public void render(Graphics g) {
		super.render(g);
		ticks++;
		if (ticks > tickl) {
			ticks = 0;
			pos++;
			if (pos > 0)
				pos = 0;
		}
	}

	@Override
	protected void drawfg(Graphics g) {
		if (main.keyboard.keys[KeyEvent.VK_0]) {
			pos = -20;
		}
		g.setColor(main.fgColor);
		g.setFont(main.TITLE_FONT);
		g.drawString("Cave Race", w / 2 - 37 - 17, h / 2 - 14 + pos + 4);
		g.setFont(main.FONT);
		g.drawString("Press Start", w / 2 - 38, h / 2 + 10 - (int)(pos * 3));
	}

}