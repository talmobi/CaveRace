package com.heartpirates.CaveRace.screens;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import com.heartpirates.CaveRace.CaveRace;
import com.heartpirates.CaveRace.CaveRace.State;
import com.heartpirates.CaveRace.Jeeves;
import com.heartpirates.CaveRace.Level;
import com.heartpirates.CaveRace.Replay;
import com.heartpirates.CaveRace.net.Client;
import com.heartpirates.CaveRace.net.Data;

public class HighScoreScreen extends Screen implements ActionListener {

	int sel = 0;

	long now = System.currentTimeMillis();
	int pressDelay = 100;

	public HighScoreScreen(CaveRace main, int w, int h) {
		super(main, w, h);
	}

	int oldSel = sel;

	List<Replay> list = null;

	@Override
	public void tick() {
		if (!(System.currentTimeMillis() - now > pressDelay))
			return;
		boolean[] keys = game.keyboard.keys;

		int max = 0;
		if (list != null)
			max = list.size() - 1;

		if (keys[KeyEvent.VK_ENTER] && list != null) {
			now = System.currentTimeMillis();

			if (list == null)
				return;

			playBlip();

			Replay rep = list.get(sel);
			game.replay = rep;

			Level newLevel = new Level(game, CaveRace.WIDTH, CaveRace.HEIGHT,
					rep.seed);
			game.level = newLevel;

			game.setGameState(State.PLAY_REPLAY);

			return;
		}

		if (keys[KeyEvent.VK_DOWN] && list != null) {
			now = System.currentTimeMillis();
			sel++;
			if (sel > max)
				sel = max;
		}

		if (keys[KeyEvent.VK_UP] && list != null) {
			now = System.currentTimeMillis();
			sel--;
			if (sel < 0)
				sel = 0;
		}

		if (keys[KeyEvent.VK_BACK_SPACE] || keys[KeyEvent.VK_ESCAPE]) {
			now = System.currentTimeMillis();
			playBlip();
			game.setGameState(State.MENU);
		}

		if (sel != oldSel) {
			playBlip();
		}
		oldSel = sel;
	}

	int lastOff = 0;
	int ytrans = 0;
	int ylimit = 10;

	@Override
	protected void drawfg(Graphics g) {
		int yy = 10;

		int off = 0;
		int ylimit = 10;

		if (sel > 3)
			off = sel - 3;

		if (lastOff != off) {
			ytrans++;
			ytrans++;
			if (ytrans > ylimit) {
				ytrans = 0;
				lastOff = off;
			}
		}

		for (int i = ((lastOff < off) ? 0 : -1); i < 7; i++) {
			if (list == null)
				break;
			int index = i + lastOff;
			if (index < 0 || index >= list.size())
				continue;
			Replay r = list.get(index);
			if (r == null)
				continue;
			g.setColor(new Color(0x94B51E));
			int x = 10;
			int y = 10 + i * 10 + yy + ((lastOff > off) ? ytrans : -ytrans);
			int nx = 0;
			if (i == sel - lastOff)
				nx += 4;

			g.drawString(r.name.substring(0, Math.min(6, r.name.length())), x
					+ nx, y);
			FontRenderContext frc = g.getFontMetrics().getFontRenderContext();
			String scoreString = "" + r.length * 100;
			Rectangle2D r2d = g.getFont().getStringBounds(scoreString, frc);
			g.drawString(scoreString, (int) (x + 100 - r2d.getWidth()), y);

			int nn = i;
			if (nn < 0)
				nn = 0;
			int ship = (r.ship + nn) % 6;
			if (ship == 5)
				x += 0;

			// draw ships for debug
			// BufferedImage img = Jeeves.i.ships[ship][0];
			BufferedImage img = Jeeves.i.ships[r.ship][0];
			g.drawImage(img, x - 9 + nx, y - 9, img.getWidth(),
					img.getHeight(), null);
		}

		g.setColor(game.bgColor);
		g.fillRect(0, 0, 120, 11);
		g.setColor(game.fgColor);
		g.drawString("NAME     SCORE", 10, 10);
	}

	Client client = null;

	@Override
	public void onSwitch() {
		now = System.currentTimeMillis() + 150;
		client = new Client(CaveRace.network);
		client.addListener(this);

		// client.postReplay = game.getAppData().getHighScoreReplay(
		// game.level.world);
		// client.start();
		final Data data = new Data();
		data.id = Data.ID.POST;
		
		Replay replay = game.getAppData().getHighScoreReplay(game.level.world);
		data.replays = new Replay[] { replay };
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				client.send(data);
				client.getTop();
			}
		}).start();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("ACTIION PERFORMED");
		if (client != null) {
			this.list = client.getReplays();

			if (this.list != null && !this.list.isEmpty()) {
				// add to local memory
				game.addTopReplays(this.list);
			}
		}
	}

}