package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.game.component.Bounds;
import net.mostlyoriginal.game.component.Edible;
import net.mostlyoriginal.game.component.Swarm;

/**
 * Created by EvilEntity on 12/12/2015.
 */
public class SwarmEatSystem extends IteratingSystem {
	private ComponentMapper<Pos> mPos;
	private ComponentMapper<Bounds> mCircleBounds;
	private ComponentMapper<Edible> mEdible;
	private ComponentMapper<Swarm> mSwarm;
	private ComponentMapper<Tint> mTint;
	private EntitySubscription edibles;

	public SwarmEatSystem () {
		super(Aspect.all(Pos.class, Swarm.class));
	}

	@Override protected void initialize () {
		edibles = world.getAspectSubscriptionManager()
			.get(Aspect.all(Pos.class, Edible.class, Bounds.class));
	}

	private Circle cb = new Circle();
	@Override protected void process (int entityId) {
		Pos pos = mPos.get(entityId);
		Swarm swarm = mSwarm.get(entityId);
		Bounds sb = mCircleBounds.get(entityId);
		cb.set(pos.getX() + sb.radius, pos.getY() + sb.radius, sb.radius);
		// we want to consume all entities that overlap the swarm, at rate decided by its scale and maybe size?
		IntBag entities = edibles.getEntities();

		for (int i = 0; i < entities.size(); i++) {
			eat(swarm, cb, entities.get(i));
		}
	}

	private Circle cb2 = new Circle();
	private void eat (Swarm swarm, Circle cb, int eid) {
		Pos pos = mPos.get(eid);
		Bounds eb = mCircleBounds.get(eid);
		cb2.set(pos.getX() + eb.radius, pos.getY() + eb.radius, eb.radius);
		// check if we overlap the edible
		if (!cb.overlaps(cb2)) return;
		Edible edible = mEdible.get(eid);
		// TODO figure it out
		float dmg = (1.1f-swarm.scale) * swarm.count/100;
		Gdx.app.log("", "dmg " + dmg);
		edible.health -= dmg;
		Tint tint = mTint.get(eid);
		float a = edible.health/edible.maxHealth;
		tint.set(1, a, a, 1);
		if (edible.health <= 0) {
			swarm.mass += edible.mass;
			edible.mass = 0;
		}

	}
}
