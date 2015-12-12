package net.mostlyoriginal.game.system.view;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.Animation;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class OneAssetSystem extends AbstractAssetSystem {

	public OneAssetSystem () {
		super("one.png");
	}

	@Override
	protected void initialize() {
		super.initialize();

		final Animation one = add("one", 0, 0, 3, 3, 4);
		one.setFrameDuration(1/5f);
	}
}
