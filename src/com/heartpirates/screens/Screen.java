package com.heartpirates.screens;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.heartpirates.Main;

public class Screen {

	BufferedImage img;

	int w, h;
	final Main main;

	public Screen(Main main, int w, int h) {
		this.main = main;
		this.w = w;
		this.h = h;
		this.img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	}
	
	public void onSwitch() {
	}

	public void tick() {
	}
	
	public void render(Graphics g) {
		// background
		g.setColor(main.bgColor);
		g.fillRect(0, 0, w, h);
		drawbg();

		// draw text
		g.setColor(main.fgColor);
		g.setFont(main.FONT);
		drawfg(g);
	}

	protected void drawfg(Graphics g) {
	}

	protected void drawbg() {
	}
}