package si.um.feri.kompajler;

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

import si.um.feri.kompajler.assets.AssetDescriptors;
import si.um.feri.kompajler.screen.GameplayScreen;
import si.um.feri.kompajler.screen.MapScreen;
import si.um.feri.kompajler.utils.Constants;
import si.um.feri.kompajler.screen.PreGameplayScreen;

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
        assetManager.load(AssetDescriptors.SS_TEXT);
        assetManager.finishLoading();
        // tu laodaj ce mas kake assete za loadat

        Vector2 position = new Vector2(Constants.MAP_WIDTH / 2f, Constants.MAP_HEIGHT / 2f);
        MapScreen mapScreen = new MapScreen(this, null);
        mapScreen.fromBefore = false;
        setScreen(mapScreen); //to je temporary pol naj bo nastavleno na main screen al kaj pac bo
        // setScreen(new PreGameplayScreen(this)); //to je temporary pol naj bo nastavleno na main screen al kaj pac bo


        // GAME INFO
        System.out.println("\n\n|-----------------------------------------------|");
        System.out.println("|                  GAME CONTROLS                |");
        System.out.println("|-----------------------------------------------|");
        System.out.println("|  Green player moves with WASD, shoots with Q  |");
        System.out.println("|Red player moves with ARROW KEYS, shoots with M|");
        System.out.println("|-----------------------------------------------|");
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

    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
