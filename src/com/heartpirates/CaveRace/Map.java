package com.heartpirates.CaveRace;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Map {

	int width, height;
	boolean[][] map;
	boolean[][] bgMap;
	
	double distribution = 0.35;

	int low_limit = 4; // def 2
	int top_limit = 7; // 4
	int resurrection_limit = 4; // 3

	private Random rand = new Random();
	private Random bgRand = new Random();

	List<Point> path = null;
	boolean showPath = false; // draw blue path

	private int size = 3;
	private long seed;
	private long bgSeed = 512;

	private final CaveRace main;

	Map(CaveRace main, int w, int h, long seed) {
		this.main = main;
		this.seed = seed;
		this.width = w;
		this.height = h;
		map = new boolean[w][h];
		bgMap = new boolean[w][h];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				map[i][j] = true;
				bgMap[i][j] = true;
			}
		}
		rand.setSeed(seed);
		bgRand.setSeed(seed + bgSeed);
	}

	public Map(CaveRace main, int w, int h, long seed, int size) {
		this.main = main;
		this.seed = seed;
		this.width = w;
		this.height = h;
		this.size = size;
		map = new boolean[w][h];
		bgMap = new boolean[w][h];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				map[i][j] = true;
				bgMap[i][j] = true;
			}
		}
		rand.setSeed(seed);
		bgRand.setSeed(seed + bgSeed);
	}

	Map initMap() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				double d = distribution;
				if ((j < height / 2 && j > 10)
						|| (i < width / 6 || i > width * 5 / 6
								&& j > height / 3 && j < height * 2 / 3)) {
					d *= 0.5;
					if (i < width / 4 || i > width * 3 / 4 && j > height / 3
							&& j < height * 2 / 3) {
						d *= 0.8;
					}
				} else if (j > height / 2) {
					int ww = width / 2;
					int dd = Math.abs(ww - i);
					d *= 1.2 + (j / height) * (j / height) * dd / width;
				}
				if (d > 0.95)
					d = 0.95;
				map[i][j] = (rand.nextDouble() < d);
				double bgd = (d * 0.26);
				if (bgRand.nextDouble() < 0.15) {
					bgd *= 2.6;
					while (bgd < .5)
						bgd += 0.13;
					if (bgd > 0.8)
						bgd = 0.8;
				}
				bgMap[i][j] = (bgRand.nextDouble() < bgd);
			}
		}

		// flip map
		double fd = rand.nextDouble();
		if (fd < 0.49) {
			boolean[][] mm = new boolean[width][height];
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					mm[i][height - j - 1] = map[i][j];
				}
			}
			map = mm;
			// System.out.println("flipped");
		}

		return this;
	}

	private boolean pathExists() {
		int s = this.size;
		if (s < 1)
			s = 1;
		if (s > 5)
			s = 5;

		int ww = width / s;
		int hh = height / s;

		boolean[][] cells = new boolean[ww][hh];

		for (int i = 0; i < ww; i++) {
			for (int j = 0; j < hh; j++) {
				cells[i][j] = true;
			}
		}

		for (int i = 0; i < ww; i++) {
			for (int j = 0; j < hh; j++) {
				for (int x = -s; x < s; x++) {
					for (int y = -s; y < s; y++) {
						if (cells[i][j]) {
							int xx = i * s + x;
							int yy = j * s + y;
							if (xx < 0)
								xx = 0;
							if (yy < 0)
								yy = 0;
							if (xx >= width)
								xx = width - 1;
							if (yy >= height)
								yy = height - 1;

							cells[i][j] = !map[xx][yy];
						}
					}
				}
			}
		}

		List<Point> points = new LinkedList<Point>();

		// start searching path from middle
		int startHeight = hh / 2;

		// try and connect previous and next paths
		if (main.player != null) {
			List<Point> prevPath = main.level.map.path;
			Point lastPoint = prevPath.get(prevPath.size() - 1);
			int y = lastPoint.y;
			if (y > 0 || y < height - 0) {
				startHeight = y;
			}

		}

		int jj = startHeight;

		for (int j = startHeight; j < hh - 1; j++) {
			jj--;
			points.clear();
			if (cells[0][j]) {
				int y = j;
				int i = 0;
				points.add(new Point(i, y));
				while (i < ww) {
					i++;
					if (i >= (ww)) {
						path = points;
						return true;
					}
					if (cells[i][y]) {

					} else if (y > 0 && cells[i][y - 1]) {
						y--;
					} else if (y < (hh - 1) && cells[i][y + 1]) {
						y++;
					} else {
						break;
					}
					points.add(new Point(i, y));
				}
			}

			points.clear();
			if (jj > 0) {
				if (cells[0][jj]) {
					int y = jj;
					int i = 0;
					points.add(new Point(i, y));
					while (i < ww) {
						i++;
						if (i >= (ww)) {
							path = points;
							return true;
						}
						if (cells[i][y]) {

						} else if (y > 0 && cells[i][y - 1]) {
							y--;
						} else if (y < (hh - 1) && cells[i][y + 1]) {
							y++;
						} else {
							break;
						}
						points.add(new Point(i, y));
					}
				}
			}
		}

		// System.out.println("<< PATH NOT FOUND >>");
		return false;
	}

	public Map initMap(double distribution) {
		this.distribution = distribution;
//		System.out.println("d: " + this.distribution);
		return initMap();
	}

	public Map initMap(double distribution, int low, int top, int res) {
		this.distribution = distribution;
		this.low_limit = low;
		this.top_limit = top;
		this.resurrection_limit = res;
		return initMap();
	}

	// simulate a step within the map
	public Map tick(int ticks) {
		if (ticks > 100)
			ticks = 100;

		int attempts = 0;
		int ticksBackup = ticks;
		// low_limit = 4; // def 2
		// top_limit = 7; // 4
		// resurrection_limit = 4; // 3

		Map nm = getCopy();
		boolean[][] newMap = nm.map;
		boolean[][] newBgMap = Arrays.copyOf(bgMap, bgMap.length);

		do {
			attempts++;
			ticks = Math.max(1, ticks);

			if (attempts > 1) {
				for (int i = 0; i < width; i++) {
					for (int j = 0; j < height; j++) {
						map[i][j] = true;
					}
				}

				initMap();
				if (attempts > 25) {
					initMap(0.3, 2, 4, 3);
				}
				if (attempts > 50) {
					for (int i = 0; i < width; i++) {
						for (int j = 0; j < height; j++) {
							map[i][j] = true;
						}
					}
				}

				for (int i = 0; i < width; i++) {
					for (int j = 0; j < height; j++) {
						newMap[i][j] = map[i][j];
					}
				}
				ticks = Math.max(6, ticksBackup--);
			}

			while (ticks > 0) {
				ticks--;

				for (int i = 0; i < width; i++) {
					for (int j = 0; j < height; j++) {
						int count = countNeighbours(map, i, j);
						int bgCount = countNeighbours(bgMap, i, j);
						boolean alive = map[i][j];
						boolean bgAlive = bgMap[i][j];

						// real terrain
						if (alive) {
							if (count < low_limit) {
								newMap[i][j] = false; // die

							}
							if (count > top_limit) {
								newMap[i][j] = false; // die
							}
						} else { // if already dead
							if (count == resurrection_limit) {
								newMap[i][j] = true; // live
							}
						}

						// background
						if (bgAlive) {
							if (bgCount < low_limit) {
								newBgMap[i][j] = false; // die

							}
							if (bgCount > top_limit) {
								newBgMap[i][j] = false; // die
							}
						} else { // if already dead
							if (bgCount == resurrection_limit) {
								newBgMap[i][j] = true; // live
							}
						}
					}
				}

				// copy new data to old buffer
				for (int i = 0; i < width; i++) {
					for (int j = 0; j < height; j++) {
						map[i][j] = newMap[i][j];
						bgMap[i][j] = newBgMap[i][j];
					}
				}
			}
		} while (!pathExists());

		return this;
	}

	Map tick() {
		return tick(1);
	}

	int countNeighbours(boolean[][] map, int i, int j) {
		int count = 0;

		// 1 2 3
		// 4 X 5
		// 6 7 8

		for (int x = -1; x < 2; x++) {
			for (int y = -1; y < 2; y++) {
				int nx = i + x;
				int ny = j + y;
				if (x == 0 && y == 0)
					continue;
				if (ny < 0 || ny >= height) {
					count++;
				} else if (nx < 0 || nx >= width) {
					// do nothing
				} else {
					if (map[nx][ny])
						count++;
				}

			}
		}

		return count;
	}

	public int getPathSize() {
		return this.size;
	}

	public List<Point> getPath() {
		List<Point> l = new LinkedList<Point>();
		for (Point p : path) {
			l.add(new Point(p.x, p.y));
		}
		return l;
	}

	private Map getCopy() {
		Map newMap = new Map(this.main, width, height, this.seed, this.size);
		newMap.distribution = distribution;
		newMap.low_limit = low_limit;
		newMap.top_limit = top_limit;
		newMap.resurrection_limit = resurrection_limit;

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				newMap.map[i][j] = map[i][j];
			}
		}

		return newMap;
	}

	BufferedImage getImage() {
		BufferedImage bimg = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		int[] pixels = ((DataBufferInt) bimg.getRaster().getDataBuffer())
				.getData();

		int index = 0;
		Point point = null;
		if (path != null) {
			if (path.size() > index)
				point = path.get(index++);
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (!map[i][j]) { // FREE
					pixels[i + j * width] = 0xEEEEEE;
					pixels[i + j * width] = 0xB6B792;
					pixels[i + j * width] = 0xABCE8A;
					pixels[i + j * width] = 0xBED89E;

					// draw background (doesn't effect game or actual terrain)
					if (!bgMap[i][j]) { // free
						pixels[i + j * width] = 0xBED89E;
					} else { // occupied
						int r = (int) (0x8D * (1.129 + 0.08));
						int g = (int) (0xAA * (1.125 + 0.08));
						int b = (int) (0xCB * (1.132 + 0.08));
						if (r > 0xBE)
							r = 0xBE;
						if (g > 0xD8)
							g = 0xD8;
						if (b > 0x9E)
							b = 0x9E;
						pixels[i + j * width] = (r << 16 | (g << 8) | b);
					}

				} else { // WALL
					pixels[i + j * width] = 0x111111;
					pixels[i + j * width] = 0x52647A;
					pixels[i + j * width] = 0x4D5A6B;
				}

				// draw available path
				if (path != null && point != null && point.x * this.size == i
						&& point.y * this.size == j) {
					if (showPath)
						pixels[i + j * width] = 0x1111FF;
					if (path.size() > index)
						point = path.get(index++);
				}
			}
		}

		return bimg;
	}

	public int getTarget(int x) {
		x = Math.abs(x) / 3;
		int foreSight = 3;
		int index = (x / this.size) + foreSight;
		if (index >= path.size()) {
			return main.level.nextMap.path.get(index % path.size()).y;
		} else if (path == null || path.size() < index || index < 0)
			return this.height / 2;
		try {
			return this.path.get(index).y;
		} catch (Exception e) {
			e.printStackTrace();
			return this.height / 2;
		}
	}
}