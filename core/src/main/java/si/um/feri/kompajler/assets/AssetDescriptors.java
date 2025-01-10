package si.um.feri.kompajler.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AssetDescriptors {

    public static final AssetDescriptor<TextureAtlas> GAMEPLAY_ATLAS =
        new AssetDescriptor<TextureAtlas>(AssetPaths.GAMEPLAY_ATLAS, TextureAtlas.class);

    private AssetDescriptors() {
    }
}
