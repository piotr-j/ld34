package net.mostlyoriginal.game.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.user.client.Event;
import net.mostlyoriginal.game.GdxArtemisGame;
import net.mostlyoriginal.game.PlatformBridge;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(900, 600);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new GdxArtemisGame(new HtmlBridge());
        }

        public class HtmlBridge implements PlatformBridge {
                @Override public void fixRightClick () {
                        getRootPanel().sinkEvents(Event.ONCONTEXTMENU);
                        getRootPanel().addHandler(
                           new ContextMenuHandler() {@Override
                           public void onContextMenu(ContextMenuEvent event) {
                                   event.preventDefault();
                                   event.stopPropagation();
                                   log("welp", "R CLICK");
                           }
                           }, ContextMenuEvent.getType());
                }
        }
}
