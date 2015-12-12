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
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.game.component.Bounds;
import net.mostlyoriginal.game.component.Swarm;
import net.mostlyoriginal.game.component.Swarmer;

/**
 * Created by EvilEntity on 12/12/2015.
 */
public class SwarmSystem extends IteratingSystem {
	private ComponentMapper<Pos> mPos;
	private ComponentMapper<Swarm> mSwarm;
	private ComponentMapper<Bounds> mCircleBounds;
	private ComponentMapper<Swarmer> mSwarmer;
	private ComponentMapper<Tint> mTint;
	@Wire CursorSystem cs;
	@Wire CameraSystem cam;

	public SwarmSystem () {
		super(Aspect.all(Pos.class, Swarmer.class));
	}

	int swarmSize;
	int swarmId;
	@Override protected void initialize () {
		EntityEdit swarm = world.createEntity().edit();
		swarm.create(Swarm.class);
		swarm.create(Bounds.class).setRadius(10);
		swarm.create(Pos.class);
		swarmId = swarm.getEntityId();
		// we can do about 10k in gwt
		createSwarm(100);
		// hmm consume speed/dmg taken based on swarm size && scale?
		// spread out swarm takes less dmg, but eats slowly,
		// compact swarms eats fast, but is vulnerable to dmg
	}

	private void createSwarm (int count) {
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
		e.create(Pos.class).set(cs.xy);
		e.create(Renderable.class).layer = 1;
		Anim anim = e.create(Anim.class);
		anim.id = "one";
		anim.age = MathUtils.random();
		e.create(Scale.class).scale = MathUtils.random(.25f, 1.25f);
		swarmSize++;
	}

	private int addPerMass = 10;
	private float changeSpeed = 2f;
	private float scale = .5f;
	private float dstScale = .5f;
	@Override protected void begin () {
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)
			|| Gdx.input.isKeyPressed(Input.Keys.A)
			|| Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			scale = MathUtils.clamp(scale + changeSpeed * world.delta, 0.1f, 1);
		} else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)
			|| Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)
			|| Gdx.input.isKeyPressed(Input.Keys.D)
			|| Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			scale = MathUtils.clamp(scale - changeSpeed * world.delta, 0.1f, 1);
		}
		dstScale = 100 * MathUtils.log(10, swarmSize);
		// 1 - 2.5 at 100 - 100000
		cam.camera.zoom = dstScale/200;
		cam.camera.update();
		dstScale *= scale;
		// this is super piggy
		Swarm swarm = mSwarm.get(swarmId);
		swarm.radius = dstScale;
		swarm.scale = scale;
		swarm.count = swarmSize;
		// slightly smaller, as its less dense near the edge
		Bounds bounds = mCircleBounds.get(swarmId).setRadius(dstScale * 0.75f);
		Pos pos = mPos.get(swarmId);
		pos.xy.set(cs.xy).sub(bounds.radius, bounds.radius);
		int toAdd = (int)swarm.mass;
		if (toAdd >0) Gdx.app.log("", "Add " + toAdd);
		for (int i = 0; i < toAdd * addPerMass; i++) {
			createSwarmer();
		}
		swarm.mass -= toAdd;
//		for (int i = 0; i < dstScale/20; i++) {
//			if (swarmSize < 100000)
//				createSwarmer();
//		}
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
