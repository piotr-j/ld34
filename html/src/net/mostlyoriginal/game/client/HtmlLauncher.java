package net.mostlyoriginal.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import net.mostlyoriginal.game.GdxArtemisGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(900, 600);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new GdxArtemisGame();
        }
}
