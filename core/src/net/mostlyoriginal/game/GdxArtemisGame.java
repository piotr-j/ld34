package net.mostlyoriginal.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import net.mostlyoriginal.game.screen.GameScreen;

public class GdxArtemisGame extends Game {

	private static GdxArtemisGame instance;
	private PlatformBridge bridge;

	public GdxArtemisGame (PlatformBridge bridge) {
		this.bridge = bridge;
	}

	@Override
	public void create() {
		instance = this;
//		Gdx.app.setLogLevel(Application.LOG_INFO);
		bridge.fixRightClick();
		restart();
	}

	public void restart() {
		setScreen(new GameScreen());
	}

	public static GdxArtemisGame getInstance()
	{
		return instance;
	}
}
