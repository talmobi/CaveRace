package com.heartpirates;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Sprite {

	boolean remove = false;
	
	protected double x = 0;
	protected double y = 0;
	protected int w;
	protected int h;
	public BufferedImage image;

	public Sprite(int w, int h) {
		this.w = w;
		this.h = h;
		this.image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	}
	
	public void tick() {
	}

	public Sprite(BufferedImage bimg) {
		this.w = bimg.getWidth();
		this.h = bimg.getHeight();
		this.image = bimg;
	}

	public void render(Graphics g) {
		g.drawImage(image, (int) x, (int) y, w, h, null);
	}
}