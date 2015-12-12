package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntityEdit;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.game.component.CircleBounds;
import net.mostlyoriginal.game.component.Swarm;
import net.mostlyoriginal.game.component.Swarmer;

/**
 * Created by EvilEntity on 12/12/2015.
 */
public class SwarmSystem extends IteratingSystem {
	private ComponentMapper<Pos> mPos;
	private ComponentMapper<Swarm> mSwarm;
	private ComponentMapper<CircleBounds> mCircleBounds;
	private ComponentMapper<Swarmer> mSwarmer;
	private ComponentMapper<Tint> mTint;
	@Wire CursorSystem cs;

	public SwarmSystem () {
		super(Aspect.all(Pos.class, Swarmer.class));
	}

	float swarmSize;
	int swarmId;
	@Override protected void initialize () {
		EntityEdit swarm = world.createEntity().edit();
		swarm.create(Swarm.class);
		swarm.create(CircleBounds.class).setRadius(10);
		swarm.create(Pos.class);
		swarmId = swarm.getEntityId();
		// we can do about 10k in gwt
		createSwarm(100);
		// hmm consume speed/dmg taken based on swarm size && scale?
		// spread out swarm takes less dmg, but eats slowly,
		// compact swarms eats fast, but is vulnerable to dmg
	}

	private void createSwarm (int count) {
		swarmSize = count;
		for (int i = 0; i < count; i++) {
			createSwarmer();
		}
	}

	private void createSwarmer () {
		EntityEdit e = world.createEntity().edit();
		float angSpeed = MathUtils.randomTriangular(15, 180, 45);
		Swarmer swarmer = e.create(Swarmer.class);
		swarmer.setAngle(MathUtils.random(360)).setAngularSpeed(angSpeed);
		swarmer.dst = (swarmer.angularSpeed - 15)/180 ;
		swarmer.clamp = MathUtils.clamp(swarmer.dst+ MathUtils.random(-.1f, .1f), 0, 1);
		e.create(Tint.class).set(1, 1-swarmer.clamp, 0, 1);
		e.create(Pos.class);
		e.create(Renderable.class).layer = 0;
		Anim anim = e.create(Anim.class);
		anim.id = "one";
		anim.age = MathUtils.random();
		e.create(Scale.class).scale = MathUtils.random(.25f, 1.25f);
	}

	private float scale = .5f;
	private float dstScale = .5f;
	@Override protected void begin () {
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)
			|| Gdx.input.isKeyPressed(Input.Keys.A)
			|| Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			scale = MathUtils.clamp(scale + .5f * world.delta, 0.1f, 1);
		} else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)
			|| Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)
			|| Gdx.input.isKeyPressed(Input.Keys.D)
			|| Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			scale = MathUtils.clamp(scale - .5f * world.delta, 0.1f, 1);
		}
		dstScale = scale * 100 * MathUtils.log(10, swarmSize);

		// this is super piggy
		Swarm swarm = mSwarm.get(swarmId);
		swarm.radius = dstScale;
		// slightly smaller, as its less dense near the edge
		mCircleBounds.get(swarmId).setRadius(dstScale * 0.75f);
		Pos pos = mPos.get(swarmId);
		pos.xy.set(cs.xy);

//		createSwarmer();
	}

	@Override protected void process (int entityId) {
//		float scale = this.scale * swarmSize;
		Pos pos = mPos.get(entityId);
		Swarmer swarmer = mSwarmer.get(entityId);
//		swarmer.age += world.delta;
		swarmer.angle -= world.delta * swarmer.angularSpeed /(scale*10);
		if (swarmer.angle >= 360) swarmer.angle -= 360;
		if (swarmer.angle < 0) swarmer.angle += 360;
		pos.xy.set(1, 0).setAngle(swarmer.angle).scl(swarmer.dst * dstScale).add(cs.xy);

		Tint tint = mTint.get(entityId);
		tint.set(1, 1-(swarmer.clamp * scale), (swarmer.clamp * scale)/5, 1);

	}
}
