package net.mostlyoriginal.game.screen;

import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.graphics.Color;
import net.mostlyoriginal.api.screen.core.WorldScreen;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.api.system.graphics.RenderBatchingSystem;
import net.mostlyoriginal.api.system.render.AnimRenderSystem;
import net.mostlyoriginal.api.system.render.ClearScreenSystem;
import net.mostlyoriginal.game.system.BoundsRenderer;
import net.mostlyoriginal.game.system.logic.*;
import net.mostlyoriginal.game.system.view.AtlasAssetSystem;
import net.mostlyoriginal.game.system.view.GameScreenAssetSystem;
import net.mostlyoriginal.game.system.view.GameScreenSetupSystem;
import net.mostlyoriginal.game.system.view.OneAssetSystem;
import net.mostlyoriginal.plugin.OperationsPlugin;

/**
 * Example main game screen.
 *
 * @author Daan van Yperen
 */
public class GameScreen extends WorldScreen {

	public static final String BACKGROUND_COLOR_HEX = "969291";

	@Override
	protected World createWorld() {
		RenderBatchingSystem renderBatchingSystem;
		return new World(new WorldConfigurationBuilder()
			.dependsOn(OperationsPlugin.class)
			.with(
				// Replace with your own systems!
				new CameraSystem(1),
				new CursorSystem(),
				new ClearScreenSystem(Color.valueOf(BACKGROUND_COLOR_HEX)),
				new GameScreenAssetSystem(),
				new AtlasAssetSystem(),
				new GameScreenSetupSystem(),
				new SwarmSystem(),
				new EdibleSpawnSystem(),
				new SwarmEatSystem(),
				new EdibleCleanupSystem(),
				renderBatchingSystem = new RenderBatchingSystem(),
				new AnimRenderSystem(renderBatchingSystem),
				new ExpireSystem()
//				new BoundsRenderer()
			).build());
	}

}
