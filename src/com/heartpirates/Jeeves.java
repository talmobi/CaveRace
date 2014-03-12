package com.heartpirates;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.esotericsoftware.kryo.Kryo;

public class Jeeves {

	public static Jeeves i = null;

	BufferedImage tiles[][];
	public BufferedImage ships[][];

	Radio radio;
	public Kryo kryo;

	Jeeves() {
		if (i == null) {
			i = this;

			radio = new Radio();
			radio.loadMusic("mus/Swift_Ray.sap");
			loadImages();
			kryo = new Kryo();
			kryo.register(Replay.class);
		}
	}

	private void loadImages() {
		System.out.println("Loading images...");
		if (i == this) {
			tiles = loadImage(2, 2, "tiles.png");
			System.out.println(" tiles loaded.");

			ships = loadImage(10, 10, "ship.png");
			System.out.println(" ships loaded.");
		}
		System.out.println("Loading images Done.");
	}

	private BufferedImage[][] loadImage(int w, int h, String str) {
		URL url = this.getClass().getClassLoader().getResource(str);

		try {
			BufferedImage bimg = ImageIO.read(url);

			int i = bimg.getWidth() / w;
			int j = bimg.getHeight() / h;

			BufferedImage[][] bimgs = new BufferedImage[i][j];

			for (int x = 0; x < i; x++) {
				for (int y = 0; y < j; y++) {
					bimgs[x][y] = bimg.getSubimage(x * w, y * h, w, h);
				}
			}

			return bimgs;

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Loading Images Failed.");
		}

		return null;
	}

}