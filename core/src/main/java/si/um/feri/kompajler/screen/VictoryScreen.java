package si.um.feri.kompajler.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.kompajler.DigitalniDvojcek;
import si.um.feri.kompajler.assets.AssetDescriptors;
import si.um.feri.kompajler.assets.AssetPaths;
import si.um.feri.kompajler.gameplay.GameManager;

public class VictoryScreen implements Screen {
    private DigitalniDvojcek game;
    private AssetManager assetManager;
    private Texture screenTexture;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Skin skin;
    private Stage stage;
    private String winner;

    public VictoryScreen(DigitalniDvojcek game, String winner) {
        this.game = game;
        this.assetManager = game.getAssetManager();
        this.skin = assetManager.get(AssetDescriptors.UI_SKIN_NEON);
        this.winner = winner;
    }

    @Override
    public void show() {
        System.out.println("INNER: " + winner);
        if (winner.equals("GREEN")) {
            System.out.println("WINNER GREEN");
            screenTexture = new Texture(AssetPaths.WINNER_TEXTURE_GREEN);
        }
        else if (winner.equals("RED")) {
            System.out.println("WINNER RED");
            screenTexture = new Texture(AssetPaths.WINNER_TEXTURE_RED);
        }
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(1280, 960, camera);
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont(); // Use a default font or load your own

        Label continueLabel = new Label("Press Enter to Continue", labelStyle);
        continueLabel.setPosition(viewport.getWorldWidth() / 2 - continueLabel.getWidth() / 2, 20); // Position at the bottom center

        stage.addActor(continueLabel);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1); // Set clear color to black
        handleInput();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(screenTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new MapScreen(game, null));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        screenTexture.dispose();
        batch.dispose();
        stage.dispose();
    }
}
