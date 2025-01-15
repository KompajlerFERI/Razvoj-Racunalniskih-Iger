package si.um.feri.kompajler.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class AssetDescriptors {

    public static final AssetDescriptor<TextureAtlas> GAMEPLAY_ATLAS =
        new AssetDescriptor<TextureAtlas>(AssetPaths.GAMEPLAY_ATLAS, TextureAtlas.class);


    public static final AssetDescriptor<Sound> SHOOT_WAV =
        new AssetDescriptor<Sound>(AssetPaths.SHOOT_WAV, Sound.class);

    public static final AssetDescriptor<Sound> EXPLOSION_WAV =
        new AssetDescriptor<Sound>(AssetPaths.EXPLOSION_WAV, Sound.class);


    private AssetDescriptors() {
    }
}
