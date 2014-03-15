package com.heartpirates.CaveRace.screens;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import com.heartpirates.CaveRace.Audio;
import com.heartpirates.CaveRace.CaveRace;

public class Screen {

	BufferedImage img;

	int w, h;
	final CaveRace game;
	protected int[] pixels;

	public Screen(CaveRace main, int w, int h) {
		this.game = main;
		this.w = w;
		this.h = h;
		this.img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		this.pixels = ((DataBufferInt) img.getRaster().getDataBuffer())
				.getData();
	}

	public void onSwitch() {
		Audio.play("Intro");
	}

	public void tick() {
	}

	public void render(Graphics g) {
		// background
		g.setColor(game.bgColor);
		drawbg(g);

		// draw text
		g.setColor(game.fgColor);
		g.setFont(game.FONT);
		drawfg(g);
	}

	protected void drawfg(Graphics g) {
	}

	protected void drawbg(Graphics g) {
		g.fillRect(0, 0, w, h);
	}
}