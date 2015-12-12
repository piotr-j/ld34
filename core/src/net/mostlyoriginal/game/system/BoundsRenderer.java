package net.mostlyoriginal.game.system;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.system.camera.CameraSystem;
import net.mostlyoriginal.game.component.CircleBounds;

/**
 * Created by EvilEntity on 12/12/2015.
 */
public class BoundsRenderer extends IteratingSystem {
	private ComponentMapper<Pos> mPos;
	private ComponentMapper<CircleBounds> mCircleBounds;
	@Wire CameraSystem cs;
	ShapeRenderer renderer;

	public BoundsRenderer () {
		super(Aspect.all(Pos.class, CircleBounds.class));
		renderer = new ShapeRenderer();
	}

	@Override protected void begin () {
		renderer.setProjectionMatrix(cs.camera.combined);
		renderer.begin(ShapeRenderer.ShapeType.Line);
	}

	@Override protected void process (int entityId) {
		Pos pos = mPos.get(entityId);
		CircleBounds cb = mCircleBounds.get(entityId);
		renderer.setColor(Color.GOLD);
		renderer.circle(pos.getX(), pos.getY(), cb.radius, 64);
	}

	@Override protected void end () {
		renderer.end();
	}

	@Override protected void dispose () {
		renderer.dispose();
	}
}
