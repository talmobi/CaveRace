package com.heartpirates.CaveRace;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener {

	public boolean keys[] = new boolean[1024];

	final private Radio radio;

	public Keyboard(Radio r) {
		this.radio = r;
	}

	enum FPS_MODE {
		low, medium, high
	};

	private FPS_MODE fpsMode = FPS_MODE.medium;

	public FPS_MODE getFps() {
		return fpsMode;
	}

	public void update() {
		if (keys[KeyEvent.VK_1]) {
			CaveRace.gameState = CaveRace.State.TITLE;
		} else if (keys[KeyEvent.VK_2]) {
			CaveRace.gameState = CaveRace.State.MENU;
		} else if (keys[KeyEvent.VK_3]) {
			CaveRace.gameState = CaveRace.State.PLAY;
		} else if (keys[KeyEvent.VK_4]) {
			CaveRace.gameState = CaveRace.State.PAUSED;
		}

		else if (keys[KeyEvent.VK_P]) {
			this.radio.stopMusic();
		}

		else if (keys[KeyEvent.VK_R]) {
			this.radio.playMusic();
		}

		else if (keys[KeyEvent.VK_PLUS] || keys[KeyEvent.VK_ADD]) {
			this.radio.setVolume(+1);
		}

		else if (keys[KeyEvent.VK_MINUS] || keys[KeyEvent.VK_SUBTRACT]) {
			this.radio.setVolume(-1);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		keys[key] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		keys[key] = false;
	}
}