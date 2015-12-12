package net.mostlyoriginal.game.system.view;

import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;

/**
 * @author Daan van Yperen
 */
@Wire
public class AtlasAssetSystem extends AbstractAssetSystem {
	TextureAtlas atlas;
	public AtlasAssetSystem () {
		super("one.png");
		atlas = createAtlas(
			"apple.png",
			"apple-dead.png",
			"one.png"
		);
	}

	@Override
	protected void initialize() {
		super.initialize();

		final Animation one = add("one", 0, 0, 3, 3, 4);
		one.setFrameDuration(1/5f);
		final Animation apple = add("apple", 0, 0, 16, 16, 1);
		apple.setFrameDuration(1);
		final Animation apple2 = add("apple-dead", 0, 0, 6, 16, 1);
		apple2.setFrameDuration(1);
	}

	public Animation add(final String identifier, int x1, int y1, int w, int h, int repeatX, int repeatY, Texture texture, float frameDuration) {
		TextureAtlas.AtlasRegion region = atlas.findRegion(identifier);
		TextureRegion[] regions = new TextureRegion[repeatX*repeatY];
		x1 += region.getRegionX();
		y1 += region.getRegionY();
		int count = 0;
		for (int y = 0; y < repeatY; y++) {
			for (int x = 0; x < repeatX; x++) {
				regions[count++] = new TextureRegion(region.getTexture(), x1 + w * x, y1 + h * y, w, h);
			}
		}

		final Animation value = new Animation(frameDuration, regions);
		sprites.put(identifier, value);
		return value;
	}

	private TextureAtlas createAtlas(String... paths) {
		PixmapPacker packer = new PixmapPacker(2048, 2048, Pixmap.Format.RGBA8888, 2, true);
		for (String path : paths) {
			Texture texture = new Texture(path);
			TextureData data = texture.getTextureData();
			data.prepare();
			// NOTE by doing this, we might end up with conflicts, probably a bad idea in real thing
			int lastSlash = path.lastIndexOf('/');
			if (lastSlash >= 0) {
				path = path.substring(lastSlash + 1);
			}
			int lastPng = path.lastIndexOf(".png");
			if (lastPng >= 0) {
				path = path.substring(0, lastPng);
			}
			packer.pack(path, data.consumePixmap());
			texture.dispose();
		}
		TextureAtlas atlas = packer.generateTextureAtlas(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear, false);
		packer.dispose();
		return atlas;
	}

	@Override public void dispose () {
		super.dispose();
		atlas.dispose();
	}
}
