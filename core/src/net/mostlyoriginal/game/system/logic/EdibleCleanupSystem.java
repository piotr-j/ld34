package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.game.component.Bounds;
import net.mostlyoriginal.game.component.Edible;

/**
 * Created by EvilEntity on 12/12/2015.
 */
public class EdibleCleanupSystem extends IteratingSystem {
	private ComponentMapper<Edible> mEdible;
	private ComponentMapper<Pos> mPos;
	private ComponentMapper<Bounds> mBounds;

	@Wire CameraSystem cs;

	public EdibleCleanupSystem () {
		super(Aspect.all(Pos.class, Edible.class, Bounds.class));
	}

	@Override protected void initialize () {

	}

	@Override protected void process (int entityId) {
		Edible edible = mEdible.get(entityId);
		if (edible.health <= 0) {
			world.delete(entityId);
		}
		// TODO clean up if out of bounds
	}
}
