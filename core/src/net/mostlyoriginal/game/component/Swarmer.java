package net.mostlyoriginal.game.component;

import com.artemis.Component;

/**
 * Created by EvilEntity on 12/12/2015.
 */
public class Swarmer extends Component {
	public float angle;
	public float age;
	public float angularSpeed;
	public float dst;
	public float clamp;

	public Swarmer setAngle (float angle) {
		this.angle = angle;
		return this;
	}

	public Swarmer setAngularSpeed (float angularSpeed) {
		this.angularSpeed = angularSpeed;
		return this;
	}
}
