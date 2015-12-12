package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntityEdit;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.game.component.Bounds;
import net.mostlyoriginal.game.component.Edible;
import net.mostlyoriginal.game.component.Expire;

/**
 * Created by EvilEntity on 12/12/2015.
 */
public class EdibleCleanupSystem extends IteratingSystem {
	private ComponentMapper<Edible> mEdible;
	private ComponentMapper<Pos> mPos;
	private ComponentMapper<Scale> mScale;
	private ComponentMapper<Bounds> mBounds;

	@Wire CameraSystem cs;

	public EdibleCleanupSystem () {
		super(Aspect.all(Pos.class, Edible.class, Bounds.class));
	}

	@Override protected void initialize () {

	}

	@Override protected void process (int entityId) {
		Pos pos = mPos.get(entityId);
		Bounds bounds = mBounds.get(entityId);
		bounds.b.setPosition(pos.xy.x + bounds.radius, pos.xy.y + bounds.radius);
		Edible edible = mEdible.get(entityId);
		if (edible.health <= 0) {
			Scale scale = mScale.get(entityId);
			EntityEdit dead = world.createEntity().edit();
			dead.create(Pos.class).set(pos);
			dead.create(Scale.class).set(scale);
			dead.create(Renderable.class);
			dead.create(Expire.class).delay = 2;
			Anim anim = dead.create(Anim.class);
			anim.id = "apple-dead";

			world.delete(entityId);
		}
		// TODO clean up if out of bounds
	}
}
