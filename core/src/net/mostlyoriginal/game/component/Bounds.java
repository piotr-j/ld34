package net.mostlyoriginal.game.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Circle;

/**
 * Created by EvilEntity on 12/12/2015.
 */
public class Bounds extends Component {
	public final Circle b = new Circle();
	public float radius;

	public Bounds setRadius(float radius) {
		b.setRadius(radius);
		this.radius = radius;
		return this;
	}

}
