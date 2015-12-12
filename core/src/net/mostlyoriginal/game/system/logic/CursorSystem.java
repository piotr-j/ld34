package net.mostlyoriginal.game.system.logic;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import net.mostlyoriginal.api.system.camera.CameraSystem;

/**
 * Created by EvilEntity on 12/12/2015.
 */
public class CursorSystem extends BaseSystem {

	@Wire CameraSystem cameraSystem;
	public Vector2 xy = new Vector2();
	private Vector3 tmp = new Vector3();

	@Override protected void processSystem () {
		tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		cameraSystem.camera.unproject(tmp);
		xy.set(tmp.x, tmp.y);
	}
}
