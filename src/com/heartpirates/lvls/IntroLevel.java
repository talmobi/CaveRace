package com.heartpirates.lvls;

import com.heartpirates.Level;
import com.heartpirates.Main;
import com.heartpirates.Map;

public class IntroLevel extends Level {
	
	int world = 2;

	public IntroLevel(Main main, int w, int h) {
		super(main, w, h);
		this.seed = 666L;
		this.mapCount = 100000;
		this.mapCounter = 100000;
		this.pathSize = 1;
		this.mapLimit = 4;
	}

	@Override
	public void updateAutopilots() {
	}

	@Override
	protected Map newMap() {
//		this.state = 3;

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
			map.initMap(d, 5, 8, 3);
			map.tick(getRandomTick());
			if (mapCount >= len * 2) {
				state = 2;
			}
		} else if (state == 2) {
			double d = 0.65 + mapCount / 380.0; // 80
			if (d > .75)
				d = .75;
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
			double d = 0.065 + mapCount / 9000.0; // 80
			if (d > 0.125)
				d = 0.125;
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
}