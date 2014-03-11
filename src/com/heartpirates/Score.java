package com.heartpirates;

import java.awt.Graphics;
import java.awt.Graphics2D;

public class Score {

	public void render(Graphics gg) {
		Graphics g = (Graphics2D) gg;

		g.drawString("Score", 5, 5);
	}
}