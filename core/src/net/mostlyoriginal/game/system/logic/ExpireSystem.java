package net.mostlyoriginal.game.system.logic;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import net.mostlyoriginal.game.component.Expire;

/**
 * Created by EvilEntity on 12/12/2015.
 */
public class ExpireSystem extends IteratingSystem {
	private ComponentMapper<Expire> mExpire;
	public ExpireSystem () {
		super(Aspect.all(Expire.class));
	}

	@Override protected void process (int entityId) {
		Expire expire = mExpire.get(entityId);
		expire.delay -= world.delta;
		if (expire.delay < 0) {
			world.delete(entityId);
		}
	}
}
