package main.java.si.um.feri.kompajler.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.audio.Sound;

public class AssetDescriptors {
    public static final AssetDescriptor<BitmapFont> UI_FONT_TITLE =
        new AssetDescriptor<BitmapFont>(AssetPaths.UI_FONT_TITLE, BitmapFont.class);
  
    public static final AssetDescriptor<BitmapFont> UI_FONT_LABEL =
        new AssetDescriptor<BitmapFont>(AssetPaths.UI_FONT_LABEL, BitmapFont.class);
  
    public static final AssetDescriptor<BitmapFont> SS_TEXT =
        new AssetDescriptor<BitmapFont>(AssetPaths.SS_TEXT, BitmapFont.class);
  
    public static final AssetDescriptor<Skin> UI_SKIN =
        new AssetDescriptor<Skin>(AssetPaths.UI_SKIN, Skin.class);

    public static final AssetDescriptor<TextureAtlas> GAMEPLAY_ATLAS =
        new AssetDescriptor<TextureAtlas>(AssetPaths.GAMEPLAY_ATLAS, TextureAtlas.class);


    public static final AssetDescriptor<Sound> SHOOT_WAV =
        new AssetDescriptor<Sound>(AssetPaths.SHOOT_WAV, Sound.class);

    public static final AssetDescriptor<Sound> EXPLOSION_WAV =
        new AssetDescriptor<Sound>(AssetPaths.EXPLOSION_WAV, Sound.class);

    private AssetDescriptors() {
    }
}
