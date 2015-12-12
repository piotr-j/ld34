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
import net.mostlyoriginal.game.component.Swarmer;

/**
 * Created by EvilEntity on 12/12/2015.
 */
public class SwarmSystem extends IteratingSystem {
	private ComponentMapper<Pos> mPos;
	private ComponentMapper<Swarmer> mSwarmer;
	@Wire CursorSystem cs;

	public SwarmSystem () {
		super(Aspect.all(Pos.class, Swarmer.class));
	}

	@Override protected void initialize () {
		createSwarm(1000);
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
		swarmer.dst = (swarmer.angularSpeed - 15)/180;
		e.create(Tint.class).set(1-swarmer.dst, swarmer.dst/2, swarmer.dst, 1);
		e.create(Pos.class);
		e.create(Renderable.class).layer = 0;
		e.create(Anim.class).id = "one";
		e.create(Scale.class).scale = MathUtils.random(.5f, 1.5f);
	}

	private float scale = 100;
	@Override protected void begin () {
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
			scale = MathUtils.clamp(scale + 50 * world.delta, 20, 220);
		} else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
			scale = MathUtils.clamp(scale - 50 * world.delta, 20, 220);
		}
	}

	@Override protected void process (int entityId) {
		Pos pos = mPos.get(entityId);
		Swarmer swarmer = mSwarmer.get(entityId);
		swarmer.age += world.delta;
		swarmer.angle += world.delta * swarmer.angularSpeed;
		if (swarmer.angle >= 360) swarmer.angle -= 360;
		if (swarmer.angle < 0) swarmer.angle += 360;
		pos.xy.set(1, 0).setAngle(swarmer.angle).scl(swarmer.dst * scale).add(cs.xy);
	}
}
