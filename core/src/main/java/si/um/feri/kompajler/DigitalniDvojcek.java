package main.java.si.um.feri.kompajler;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Map;

import main.java.si.um.feri.kompajler.assets.AssetDescriptors;
import main.java.si.um.feri.kompajler.screen.GameplayScreen;
import main.java.si.um.feri.kompajler.screen.MapScreen;
import main.java.si.um.feri.kompajler.utils.Constants;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class DigitalniDvojcek extends Game {
    private SpriteBatch batch;
    private Texture image;
    public AssetManager assetManager;

    // Dev branch :D

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");

        assetManager = new AssetManager();
        assetManager.load(AssetDescriptors.UI_SKIN);
        assetManager.finishLoading();
        // tu laodaj ce mas kake assete za loadat

        Vector2 position = new Vector2(Constants.MAP_WIDTH / 2f, Constants.MAP_HEIGHT / 2f);
        MapScreen mapScreen = new MapScreen(this, null);
        mapScreen.fromBefore = false;
        setScreen(mapScreen); //to je temporary pol naj bo nastavleno na main screen al kaj pac bo
    }

//    @Override
//    public void render() {
//
//
//        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
//        batch.begin();
//        batch.draw(image, 140, 210);
//        batch.end();
//    }
    public SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
