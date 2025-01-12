package si.um.feri.kompajler;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import si.um.feri.kompajler.screen.GameplayScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class DigitalniDvojcek extends Game {
    private SpriteBatch batch;
    private Texture image;

    private AssetManager assetManager;


    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");

        assetManager = new AssetManager();
        // tu laodaj ce mas kake assete za loadat

        assetManager.finishLoading();

        setScreen(new GameplayScreen(this)); //to je temporary pol naj bo nastavleno na main screen al kaj pac bo
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
