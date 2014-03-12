package com.heartpirates;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Level {

	double x = 0;
	double y = 0;

	int width, height;
	int sw; // stretch width
	boolean[][] blockMap = null;

	double lt = 0.04; // low threshold
	double ht = 0.09; // high threshold

	int tick_lt = 22;
	int tick_ht = 30;

	int mapCount = 0;
	long score = 0;

	int loop = 0;
	int state = 0;

	int low_limit = 4; // def 2
	int top_limit = 7; // 4
	int resurrection_limit = 4; // 3

	BufferedImage img = null;
	BufferedImage nextImg = null;

	long startTime = System.currentTimeMillis();

	private final Random random = new Random();

	Map map;
	Map nextMap;
	
	public int tickCount = 0;

	double speed = 1.6;
	long seed = 1L;
	private int pathSize = 5;

	private final Main main;

	public Level(Main main, int w, int h) {
		this.main = main;
		this.random.setSeed(this.seed);
		this.width = w;
		this.height = h;
		map = newMap();
		nextMap = newMap();
		img = map.getImage();
		nextImg = nextMap.getImage();
		blockMap = new boolean[width][height];
		updateBlockmap();
		updateAutopilots();

		initLevel();
	}

	public void updateAutopilots() {
		if (main.autopilot != null) {
			// main.autopilot.addPath(map.getPath());
			main.autopilot.setTarget(map.getTarget((int) x - 85));
		}
	}

	public void tick() {
		tickCount++;
		// x -= Math.min(1, speed + (mapCount / 100.0));
		x -= speed;
		score++;

		if (x < -(Main.SCREEN_WIDTH)) {
			// System.out.println("click");
			mapCount++;
			if (speed < 3)
				speed += 0.2;
			else
				speed = 3;
			x = 0;
			nextLevel2();
			double map_value = (double) (5.0 / pathSize)
					* (1.0 + (Math.sqrt(mapCount)));
			score += (int) map_value;
			calculatePathSize();

			// buffer more maps if necessary
			if (levelThread != null)
				levelThread.interrupt();
		}

		updateBlockmap();
		updateAutopilots();
		
	}

	List<Map> mapList = new LinkedList<Map>();
	List<BufferedImage> mapImgList = new LinkedList<BufferedImage>();
	int mapCounter = 0;
	int mapLimit = 10;
	Thread levelThread = null;

	private void initLevel() {
		// load 100 maps beforehand
		for (int i = 0; i < mapLimit; i++) {
			Map map = newMap();
			BufferedImage mapImg = map.getImage();

			mapList.add(map);
			mapImgList.add(mapImg);
		}

		levelThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					while (mapList.size() < mapLimit) {
						Map map = newMap();
						BufferedImage mapImg = map.getImage();
						mapList.add(map);
						mapImgList.add(mapImg);
						System.out.printf("Added map. [%s/%s]\n", mapList.size(), mapLimit);
					}
					
					try {
						Thread.sleep(Integer.MAX_VALUE);
					} catch (InterruptedException ie) {
//						System.out.println("Thread Interrupted.");
					}
				}
			}
		});

		levelThread.setPriority(Thread.MIN_PRIORITY);
		levelThread.start();
		
		nextLevel2();
		nextLevel2();
	}

	private void nextLevel2() {
		if (mapList.size() > 0) {
			map = nextMap;
			nextMap = mapList.remove(0);
			img = nextImg;
			nextImg = mapImgList.remove(0);
		}
	}

	private void nextLevel1() {
		map = nextMap;
		nextMap = newMap();
		img = map.getImage();
		nextImg = nextMap.getImage();
	}

	private void calculatePathSize() {
		if (mapCount < 20) {
			pathSize = 5;
		} else if (mapCount >= 20 && mapCount < 40) {
			pathSize = 4;
		} else if (mapCount >= 40 && mapCount < 80) {
			pathSize = 4;
		} else if (mapCount >= 80 && mapCount < 120) {
			pathSize = 3;
		} else if (mapCount >= 180 && mapCount < 260) {
			pathSize = 2;
		} else if (mapCount >= 260) {
			pathSize = 2;
		}

	}

	private void updateBlockmap() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int xx = (int) (x / 3);
				if (i < (map.width + xx)) {
					int xp = (i - xx);
					blockMap[i][j] = map.map[xp][j];
				} else {
					int xp = (i - map.width - xx);
					blockMap[i][j] = nextMap.map[xp][j];
				}
			}
		}
	}

	int newMapCounter = 0;

	private Map newMap() {
		int mapCount = newMapCounter++;

		double maxg = 0.80;
		int len = 3;
		Map map = new Map(this.main, width, height, random.nextLong(), pathSize);
		if (state == 0) {
			double d = 0.35 + mapCount / 180.0; // 80
			if (d > 0.6)
				d = 0.6;
			map.initMap(d);
			if (mapCount >= len) {
				state = 1;
			}
			map.tick(getRandomTick());
		} else if (state == 1) {
			double d = 0.65 + mapCount / 200.0; // 80
			if (d > maxg)
				d = maxg;
			map.initMap(d, 4, 7, 4);
			map.tick(getRandomTick());
			if (mapCount >= len * 2) {
				state = 2;
			}
		} else if (state == 2) {
			double d = 0.45 + mapCount / 380.0; // 80
			if (d > maxg)
				d = maxg;
			map.initMap(d);
			if (mapCount >= len * 3) {
				state = 3;
			}
			map.tick(getRandomTick());
		} else if (state == 3) {
			double d = 0.08 + mapCount / 9000.0; // 80
			if (d > 0.16)
				d = 0.16;
			map.initMap(d, 3, 6, 3);
			if (mapCount >= len * 4) {
				state = 4;
			}
			map.tick(getRandomTick());
		} else if (state == 4) {
			double d = 0.04 + mapCount / 9000.0; // 80
			if (d > 0.08)
				d = 0.08;
			map.initMap(d, 2, 5, 3);
			if (mapCount >= len * 5) {
				state = 5;
			}
			map.tick(getRandomTick());
		} else if (state == 5) { // pyramids
			double d = 0.001 + mapCount / 9000.0; // 80
			if (d > 0.025)
				d = 0.025;
			map.initMap(d, 2, 8, 3);
			if (mapCount >= len * 6) {
				state = 6;
			}
			map.tick(20 + Math.min(loop, 22));
		} else if (state == 6) {
			double d = 0.035 + mapCount / 9000.0; // 80
			if (d > 0.04)
				d = 0.04;
			map.initMap(d, 2, 4, 3);
			if (mapCount >= len * 7) {
				state = 7;
			}
			map.tick(getRandomTick());
		} else if (state == 7) {
			double d = 0.1225 + mapCount / 5000.0; // 80
			if (d > 0.13)
				d = 0.13;
			map.initMap(d, 3, 7, 3);
			if (mapCount >= len * 8) {
				state = 0;
				loop++;
			}
			map.tick(8);
		}

		return map;
	}

	private double getRandomDist() {
		return (random.nextDouble() * (ht - lt)) + lt;
	}

	private int getRandomTick() {
		return (int) ((random.nextDouble() * (tick_ht - tick_lt)) + tick_lt + ((int) (Math
				.min(loop, 15))));
	}

	public boolean isBlocked(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return true;
		if (blockMap == null)
			return false;
		return blockMap[x][y];
	}

	public void render(Graphics g) {
		int x = (int) (this.x);
		int y = (int) (this.y);
		g.drawImage(img, x, y, width, height, null);
		g.drawImage(nextImg, x + width, y, width, height, null);
	}

}