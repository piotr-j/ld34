package net.mostlyoriginal.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.mostlyoriginal.game.GdxArtemisGame;
import net.mostlyoriginal.game.PlatformBridge;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 900;
		config.height = 600;
		new LwjglApplication(new GdxArtemisGame(new DesktopBridge()), config);
	}

	public static class DesktopBridge implements PlatformBridge {
		@Override public void fixRightClick () {

		}
	}
}
