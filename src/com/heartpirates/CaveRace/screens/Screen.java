package com.heartpirates.CaveRace.screens;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import com.heartpirates.CaveRace.Audio;
import com.heartpirates.CaveRace.Main;

public class Screen {

	BufferedImage img;

	int w, h;
	final Main main;
	protected int[] pixels;

	public Screen(Main main, int w, int h) {
		this.main = main;
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
		g.setColor(main.bgColor);
		drawbg(g);

		// draw text
		g.setColor(main.fgColor);
		g.setFont(main.FONT);
		drawfg(g);
	}

	protected void drawfg(Graphics g) {
	}

	protected void drawbg(Graphics g) {
		g.fillRect(0, 0, w, h);
	}
}