package net.mostlyoriginal.game.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import net.mostlyoriginal.game.GdxArtemisGame;
import net.mostlyoriginal.game.PlatformBridge;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new GdxArtemisGame(new AndroidBridge()), config);
	}

	public static class AndroidBridge implements PlatformBridge {
		@Override public void fixRightClick () {

		}
	}
}
