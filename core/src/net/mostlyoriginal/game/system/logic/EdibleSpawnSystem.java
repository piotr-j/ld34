package net.mostlyoriginal.game.system.logic;

import com.artemis.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.game.component.Bounds;
import net.mostlyoriginal.game.component.Edible;
import net.mostlyoriginal.game.component.Swarm;

/**
 * Created by EvilEntity on 12/12/2015.
 */
public class EdibleSpawnSystem extends BaseSystem {
	private ComponentMapper<Edible> mEdible;

	int target = 10;
	EntitySubscription edibles;
	EntitySubscription swarm;
	@Override protected void initialize () {
		edibles = world.getAspectSubscriptionManager().get(Aspect.all(Edible.class));
		swarm = world.getAspectSubscriptionManager().get(Aspect.all(Swarm.class));
	}

	@Override protected void processSystem () {
		int size = edibles.getEntities().size();
		for (int i = 0; i < target - size; i++) {
			spawn();
		}
	}

	private void spawn () {
		// TODO type of things should be based on size of the swarm
		EntityEdit e = world.createEntity().edit();
		e.create(Pos.class).set(MathUtils.random(50, 860), MathUtils.random(50, 550));
//		e.create(Pos.class).set(450, 300);
		e.create(Renderable.class).layer = 0;
		Anim anim = e.create(Anim.class);
		anim.id = "apple";
		float scale = MathUtils.random(1f, 2f);
		e.create(Scale.class).scale = scale;
		Edible edible = e.create(Edible.class);
		edible.maxHealth = edible.health = 50 * scale;
		edible.mass = 1 * scale;
		e.create(Tint.class).set(Color.WHITE);
		Bounds bounds = e.create(Bounds.class);
		bounds.setRadius(scale * 8);
	}
}
