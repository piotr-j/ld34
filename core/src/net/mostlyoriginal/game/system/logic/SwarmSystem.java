package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntityEdit;
import com.artemis.EntitySubscription;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.game.component.Bounds;
import net.mostlyoriginal.game.component.Edible;
import net.mostlyoriginal.game.component.Swarm;
import net.mostlyoriginal.game.component.Swarmer;

/**
 * Created by EvilEntity on 12/12/2015.
 */
public class SwarmSystem extends IteratingSystem {
	private final static String TAG = SwarmSystem.class.getSimpleName();

	private ComponentMapper<Pos> mPos;
	private ComponentMapper<Swarm> mSwarm;
	private ComponentMapper<Bounds> mCircleBounds;
	private ComponentMapper<Swarmer> mSwarmer;
	private ComponentMapper<Tint> mTint;
	private FPSLogger logger;
	@Wire CursorSystem cs;
	@Wire CameraSystem cam;

	public SwarmSystem () {
		super(Aspect.all(Pos.class, Swarmer.class));
	}

	int swarmSize;
	int swarmId;
	EntitySubscription edibles;
	long startTime;
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

		edibles = world.getAspectSubscriptionManager().get(Aspect.all(Pos.class, Edible.class, Bounds.class));
		startTime = TimeUtils.nanoTime();
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

	private int addPerMass = 100;
	private float changeSpeed = 2f;
	private float scale = .5f;
	private float dstScale = .5f;
	private Vector2 sPos = new Vector2();
	private float sRadius2;
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
		float newZoom = dstScale/200;
		if (newZoom != cam.zoom) {
//			updateCamZoom(newZoom);
		}
		dstScale *= scale;
		// this is super piggy
		Swarm swarm = mSwarm.get(swarmId);
		swarm.radius = dstScale;
		swarm.scale = scale;
		swarm.count = swarmSize;
		sRadius2 = dstScale * dstScale;
		// slightly smaller, as its less dense near the edge
		Bounds bounds = mCircleBounds.get(swarmId).setRadius(dstScale * 0.75f);
		Pos pos = mPos.get(swarmId);
		pos.xy.set(cs.xy).sub(bounds.radius, bounds.radius);
		sPos.set(pos.xy.x + dstScale, pos.xy.y + dstScale);
		int toAdd = (int)swarm.mass;
//		if (toAdd >0) Gdx.app.log("", "Add " + toAdd);
		for (int i = 0; i < toAdd * addPerMass; i++) {
			createSwarmer();
		}
		swarm.mass -= toAdd;
		IntBag entities = edibles.getEntities();
		data = entities.getData();
		size = entities.size();
	}

	int[] data;
	int size;
	Vector3 zTmp1 = new Vector3();
	Vector3 zTmp2 = new Vector3();
	private void updateCamZoom (float newZoom) {
		OrthographicCamera c = cam.camera;
		c.unproject(zTmp1.set(cs.xy.x, cs.xy.y, 0));
		c.zoom = newZoom;
		c.update();
		c.unproject(zTmp2.set(cs.xy.x, cs.xy.y, 0));
		c.translate(zTmp1.sub(zTmp2));
		c.update();
	}

	@Override protected void process (int entityId) {
//		float scale = this.scale * swarmSize;
		Pos pos = mPos.get(entityId);
		Swarmer swarmer = mSwarmer.get(entityId);
//		swarmer.age += world.delta;
		swarmer.angle -= world.delta * swarmer.angularSpeed /(scale*10);
		if (swarmer.angle >= 360) swarmer.angle -= 360;
		if (swarmer.angle < 0) swarmer.angle += 360;
		if (pos.xy.dst2(sPos) <= sRadius2) {
			for (int i = 0; i < size; i++) {
				int eid = data[i];
				Bounds eb = mCircleBounds.get(eid);
				if (eb.b.contains(pos.xy)) {
					pos.set(eb.b.x, eb.b.y);
					pos.xy.add(
						MathUtils.randomTriangular(-eb.radius, eb.radius, 0),
						MathUtils.randomTriangular(-eb.radius, eb.radius, 0));
					return;
				}
			}
		}
		pos.xy.set(1, 0).setAngle(swarmer.angle).scl(swarmer.dst * dstScale).add(cs.xy);

		Tint tint = mTint.get(entityId);
		tint.set(1, 1-(swarmer.clamp * scale), (swarmer.clamp * scale)/5, 1);

	}

	@Override protected void end () {
		if (TimeUtils.nanoTime() - startTime > 1000000000) /* 1,000,000,000ns == one second */{
			int size = getEntityIds().size();
			Gdx.app.log(TAG, "fps: " + Gdx.graphics.getFramesPerSecond() + ", entities: " + size);
			startTime = TimeUtils.nanoTime();
			int max = 250000;
			if (size < max) {
				for (int i = 0; i < Math.max(size / 3, 100); i++) {
					if (size + i < max) {
						createSwarmer();
					} else {
						break;
					}
				}
			}
		}
	}
}
