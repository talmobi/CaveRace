package com.heartpirates.CaveRace;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFrame;

import com.heartpirates.CaveRace.lvls.IntroLevel;
import com.heartpirates.CaveRace.net.Network;
import com.heartpirates.CaveRace.screens.GameOverScreen;
import com.heartpirates.CaveRace.screens.HighScoreScreen;
import com.heartpirates.CaveRace.screens.MenuScreen;
import com.heartpirates.CaveRace.screens.ReplayOverScreen;
import com.heartpirates.CaveRace.screens.ReplayScreen;
import com.heartpirates.CaveRace.screens.Screen;
import com.heartpirates.CaveRace.screens.TitleScreen;

public class CaveRace extends Canvas implements Runnable {

	public static final int WIDTH = 120;
	public static final int HEIGHT = (WIDTH * 9) / 16;

	public static Network network;

	public static final int SCALE = 3;

	public static final int SCREEN_WIDTH = WIDTH * SCALE;
	public static final int SCREEN_HEIGHT = HEIGHT * SCALE;

	public static String NAME = "Cave Race";

	private boolean isRunning = false;
	private JFrame frame = null;

	public int tickCount = 0;

	int sleepTime = 1;
	Jeeves jeeves;
	public Keyboard keyboard;
	Map map;

	private Level currentLevel = null;

	public Level level;
	IntroLevel introLevel;

	public Font TITLE_FONT;
	public Font FONT;

	AppData appData;

	BufferedImage screen = new BufferedImage(WIDTH, HEIGHT,
			BufferedImage.TYPE_INT_ARGB);
	int[] pixels = ((DataBufferInt) screen.getRaster().getDataBuffer())
			.getData();

	Screen titleScreen = new TitleScreen(this, WIDTH, HEIGHT);
	MenuScreen menuScreen = new MenuScreen(this, WIDTH, HEIGHT);
	public boolean showGameOver = false;
	Screen gameOverScreen = new GameOverScreen(this, WIDTH, HEIGHT);
	Screen replayOverScreen = new ReplayOverScreen(this, WIDTH, HEIGHT);
	Screen replayScreen = new ReplayScreen(this, WIDTH, HEIGHT);
	Screen highScoreScreen = new HighScoreScreen(this, WIDTH, HEIGHT);

	BufferedImage testImg = null;

	// List<Sprite> sprites = new ArrayList<Sprite>();
	// List<Sprite> spritesBuffer = new ArrayList<Sprite>();

	Player player;
	Autopilot autopilot;
	Ghostpilot ghostpilot;

	public final Color bgColor = new Color(0x202020);
	public final Color fgColor = new Color(0xCFBFAD);

	long keyPressTime = System.currentTimeMillis();

	public enum State {
		TITLE, MENU, PLAY, PAUSED, RESTART, MENU_REPLAY, PLAY_REPLAY, MENU_HIGHSCORES
	}

	private State gameState = State.TITLE;
	private State lastState = null;

	Recorder rec = new Recorder(1L, 1);
	public Replay replay;

	public CaveRace() {
		try {
			CaveRace.network = new Network();
			
			this.TITLE_FONT = Font.createFont(
					Font.TRUETYPE_FONT,
					this.getClass().getClassLoader()
							.getResourceAsStream("titlefont.ttf")).deriveFont(
					10f);
			this.FONT = Font.createFont(
					Font.TRUETYPE_FONT,
					this.getClass().getClassLoader()
							.getResourceAsStream("font.ttf")).deriveFont(14f);
		} catch (Exception e) {
			this.FONT = null;
			e.printStackTrace();
		}

		try {
			appData = AppData.load();
		} catch (FileNotFoundException e) {
			appData = new AppData();
			appData.successfullyLoaded = true;
		} catch (IOException e) {
			appData.successfullyLoaded = false;
			e.printStackTrace();
		}
	}

	public void init() {
		if (frame == null) {
			this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
			this.setMinimumSize(this.getSize());
			this.setMaximumSize(this.getSize());
			this.setPreferredSize(this.getSize());

			frame = new JFrame(NAME);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setResizable(false);

			frame.setAlwaysOnTop(true);

			Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setLocation((int) (ss.width - SCREEN_WIDTH * 1.5),
					(int) (SCREEN_HEIGHT * 0.5 + 10));

			frame.add(this);
			frame.pack();

			jeeves = new Jeeves();
			keyboard = new Keyboard(jeeves.radio);
			level = new Level(this, WIDTH, HEIGHT, 1L);
			introLevel = new IntroLevel(this, WIDTH, HEIGHT);

			map = new Map(this, WIDTH, HEIGHT, 1L);
			map.initMap();
			testImg = map.getImage();

			player = new Player(this, Jeeves.i.ships[1][0]);
			player.x = 0;
			player.y = 10;

			autopilot = new Autopilot(this, 0, Jeeves.i.ships[3][0]);
			autopilot.x = 0;
			autopilot.y = 10;

			ghostpilot = new Ghostpilot(this, 0, Jeeves.i.ships[4][0]);
			ghostpilot.x = 15;
			ghostpilot.y = 10;

			this.addKeyListener(keyboard);

			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					saveAppData();
				}
			});

			replay = appData.getHighScoreReplay(1);
		}
	}

	public void saveAppData() {
		new Thread(new Runnable() {
			long now = System.currentTimeMillis();

			@Override
			public void run() {
				try {
					AppData.save(appData);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				System.out.println("Save took: "
						+ (System.currentTimeMillis() - now) + " ms");
			}
		}).start();
	}

	public int getMapCount() {
		if (level == null)
			return 0;
		return level.mapCount;
	}

	public void start() {
		if (!isRunning && frame != null) {
			frame.setVisible(true);
			isRunning = true;
			new Thread(this).start();
		}
	}

	// Main game loop
	@Override
	public void run() {

		int TARGET_FPS = 100;
		int MS_PER_TICK = 1000 / TARGET_FPS;

		long lastTime = System.currentTimeMillis();
		long now = lastTime;
		long lastSecond = System.currentTimeMillis();

		double delta = 0.0;

		int frames = 0;
		int ticks = 0;

		while (isRunning) {
			lastTime = now;
			now = System.currentTimeMillis();

			delta += (double) (now - lastTime) / MS_PER_TICK;

			if (delta > 5)
				delta = 5;

			boolean shouldRender = false;
			while (delta > 1) {
				shouldRender = true;
				delta--;
				tick();
				ticks++;
			}

			if (shouldRender) {
				render();
				frames++;
			}

			swap();

			if (now - lastSecond > 1000) {
				String status = "State: " + gameState + ", score: "
						+ player.rec.list.size() * 100 + ", distance: "
						+ level.mapCount + ", frames: " + frames + ", ticks: "
						+ ticks + ", map: " + level.mapCount;
				print(status);
				frame.setTitle(status);
				frames = 0;
				ticks = 0;
				lastSecond += 1000;
			}

			try {
				sleep();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}

		System.out.println("Game Closed.");
	}

	private void updateKeyboard() {
		long now = System.currentTimeMillis();
		keyboard.update();

		if (now - keyPressTime < 200)
			return;

		keyPressTime = now;
	}

	private State newState = gameState;

	private void tick() {
		updateKeyboard();
		boolean[] keys = keyboard.keys;

		if (gameState == State.PAUSED) {
		}

		else if (gameState == State.RESTART) {
			int w = level.width;
			int h = level.height;
			this.level = new Level(this, w, h, 1L);
			currentLevel = this.level;

			setGameState(State.PLAY);

			player.reset();
			ghostpilot.reset();
		}

		else if (gameState == State.TITLE) {
			if (lastState != gameState) {
				titleScreen.onSwitch();
				jeeves.radio.loadAndPlay("mus/Drunk_Chessboard.sap");
			}

			introLevel.tick();

			if (keys[KeyEvent.VK_SPACE] || keys[KeyEvent.VK_ENTER]) {
				newState = State.MENU;
			}
		}

		else if (gameState == State.MENU) {
			if (lastState != gameState) {
				menuScreen.onSwitch();
				jeeves.radio.loadAndPlay("mus/Return_of_Atarians.sap");
			}
			menuScreen.tick();
		}

		else if (gameState == State.MENU_REPLAY) {
			if (lastState != gameState) {
				replayScreen.onSwitch();
				jeeves.radio.loadAndPlay("mus/Return_of_Atarians.sap");
			}
			replayScreen.tick();
		}

		else if (gameState == State.MENU_HIGHSCORES) {
			if (lastState != gameState) {
				highScoreScreen.onSwitch();
				jeeves.radio.loadAndPlay("mus/Return_of_Atarians.sap");
			}
			highScoreScreen.tick();
		}

		else if (gameState == State.PLAY_REPLAY) {
			if (lastState != gameState) {
				prepareReplay();
				playRandomSong();
			}

			level.tick();
			ghostpilot.tick();

			// replay
			int n = level.tickCount;
			if (replay != null) {
				int gy = replay.get(n + (int) ghostpilot.x);
				if (gy < 0) {
					ghostpilot.fadeOut();
				} else {
					ghostpilot.y = gy;
				}
			} else {
				if (ghostpilot != null) {
					ghostpilot.remove = true;
				}
			}

			if (ghostpilot.remove && !showGameOver) {
				showGameOver = true;
			}

			if (keys[KeyEvent.VK_ESCAPE] || keys[KeyEvent.VK_BACK_SPACE]) {
				setGameState(State.MENU_REPLAY);
			}
		}

		else if (gameState == State.PLAY) {
			if (lastState != gameState) {
				preparePlay();
				Replay replay = null;
				switch (menuScreen.mode) {
				case PERSONAL:
					replay = appData.getHighScoreReplay(level.world);
					break;
				case WORLDWIDE:
					replay = appData.getWorldHighScoreReplay(level.world);
					break;
				case NONE:
				default:
					ghostpilot.visible = false;
					break;
				}
				this.replay = replay;
				prepareReplay();

				Audio.play("EngineStart");
				playRandomSong();
			}

			level.tick();
			player.record();
			player.tick();
			ghostpilot.tick();

			// replay
			int n = level.tickCount;
			if (replay != null) {
				int gy = replay.get(n + (int) ghostpilot.x);
				if (gy < 0) {
					ghostpilot.fadeOut();
				} else {
					ghostpilot.y = gy;
				}
			} else {
				if (ghostpilot != null) {
					ghostpilot.remove = true;
				}
			}

			if (player.remove && !showGameOver) {
				showGameOver = true;
				autoSaveReplay();
			}
		}

		lastState = gameState;
		tickCount++;
		gameState = newState;
	}

	public void prepareReplay() {
		if (replay == null)
			return;
		ghostpilot.reset();
		ghostpilot.image = Jeeves.i.ships[replay.ship][0];
		showGameOver = false;
	}

	public void preparePlay() {
		showGameOver = false;
		player.image = Jeeves.i.ships[getShipId()][0];
	}

	public void setGameState(State state) {
		this.newState = state;
	}

	private void playRandomSong() {
		jeeves.radio.stopMusic();

		int n = (int) (Math.random() * 5);
		switch (n) {
		case 0:
			jeeves.radio.loadAndPlay("mus/X_Ray_2.sap");
			break;
		case 1:
			jeeves.radio.loadAndPlay("mus/Czarna_dziura_w_dupie.sap");
			break;
		case 2:
			jeeves.radio.loadAndPlay("mus/drilldance.tm8");
			break;
		case 3:
			jeeves.radio.loadAndPlay("mus/shakeass.tm8");
			break;
		case 4:
			jeeves.radio.loadAndPlay("mus/zizibum.tm8");
			break;
		default:
			jeeves.radio.loadAndPlay("mus/Komar.sap");
		}
	}

	private int getShipId() {
		return ((MenuScreen) this.menuScreen).ship;
	}

	private void autoSaveReplay() {
		final int shipid = this.getShipId();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Replay replay = player.rec.getReplay();
				replay.ship = shipid;
				appData.addReplay(replay);
				saveAppData();
			}
		}).start();
	}

	private void render() {
		Graphics g = screen.getGraphics();

		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = 0;
		}

		if (lastState != gameState)
			return;

		switch (gameState) {
		case MENU:
			menuScreen.render(g);
			break;

		case MENU_REPLAY:
			replayScreen.render(g);
			break;
		case MENU_HIGHSCORES:
			highScoreScreen.render(g);
			break;

		case PAUSED:
		case PLAY_REPLAY:
			ghostpilot.render(g);

			if (showGameOver)
				replayOverScreen.render(g);
			currentLevel = level;
			break;
		case PLAY:
			player.render(g);
			ghostpilot.render(g);

			if (showGameOver)
				gameOverScreen.render(g);
			currentLevel = level;
			break;
		case TITLE:
			currentLevel = introLevel;
			titleScreen.render(g);
			break;
		default:
			break;
		}
	}

	private void swap() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(2);
			return;
		}

		Graphics g = bs.getDrawGraphics();

		int fw = frame.getWidth();
		int fh = frame.getHeight();
		g.setColor(bgColor);
		g.fillRect(0, 0, fw, fh);

		// draw level
		if (currentLevel != null) {
			g.drawImage(currentLevel.img, (int) currentLevel.x,
					(int) currentLevel.y, SCREEN_WIDTH, SCREEN_HEIGHT, null);
			g.drawImage(currentLevel.nextImg,
					(int) (currentLevel.x + SCREEN_WIDTH),
					(int) currentLevel.y, SCREEN_WIDTH, SCREEN_HEIGHT, null);
		}

		// draw game screen
		g.drawImage(screen, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);

		g.dispose();
		bs.show();
	}

	private void sleep() throws InterruptedException {
		Thread.sleep(2);
	}

	public void print(String str) {
		System.out.println(str);
	}

	public static void main(String[] args) {
		if (args.length > 0) {
		}

		CaveRace game = new CaveRace();
		game.init();

		try {
			game.start();
		} catch (Exception e) {
			AppData.save(game.appData, ".AppDataBackup");
		}
	}

	public int getScrollX() {
		if (level == null)
			return 0;
		return (int) level.x;
	}

	public int getMapPathSize() {
		if (level == null)
			return 5;
		if (level.map == null)
			return 5;
		return level.map.getPathSize();
	}

	public AppData getAppData() {
		return this.appData;
	}

	public void addTopReplays(List<Replay> list) {
		this.appData.topReplays = list;
		saveAppData();
	}

	public static void sort(List<Replay> tempReplays) {
		Collections.sort(tempReplays, new Comparator<Replay>() {
			@Override
			public int compare(Replay r1, Replay r2) {
				if (r1.length < r2.length)
					return 1;
				return -1;
			}
		});
	}
}
