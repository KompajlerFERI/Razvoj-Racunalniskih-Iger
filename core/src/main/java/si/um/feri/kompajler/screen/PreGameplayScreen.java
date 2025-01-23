// PreGameplayScreen.java
package si.um.feri.kompajler.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
import si.um.feri.kompajler.config.GameConfig;

public class PreGameplayScreen implements Screen {
    private DigitalniDvojcek game;
    private AssetManager assetManager;
    private Texture preGameScreenTexture;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Skin skin;
    private Stage stage;
    private TextField player1Field;
    private TextField player2Field;
    private TextButton playButton;

    public PreGameplayScreen(DigitalniDvojcek game) {
        this.game = game;
        this.assetManager = game.getAssetManager();
        this.skin = assetManager.get(AssetDescriptors.UI_SKIN_NEON);
    }

    @Override
    public void show() {
        preGameScreenTexture = new Texture(AssetPaths.PRE_GAME_SCREEN);
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT, camera); // Adjust the viewport size as needed
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

        player1Field = new TextField("", skin);
        player2Field = new TextField("", skin);
        playButton = new TextButton("Play", skin);
        playButton.setDisabled(true);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();
        mainTable.add(playButton).colspan(2).pad(10).width(150).height(50);

        Table inputTable = new Table();
        inputTable.add(player2Field).width(200).pad(10).left().expandX().padRight(280);
        inputTable.add(player1Field).width(200).pad(10).right().expandX().padLeft(280);
        inputTable.setPosition(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2 - 150);

        stage.addActor(mainTable);
        stage.addActor(inputTable);

        player1Field.addListener(event -> {
            updatePlayButtonState();
            return false;
        });

        player2Field.addListener(event -> {
            updatePlayButtonState();
            return false;
        });

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String player1Name = player1Field.getText();
                String player2Name = player2Field.getText();
                game.setScreen(new GameplayScreen(game, player1Name, player2Name));
            }
        });
    }

    private void updatePlayButtonState() {
        boolean isPlayer1Set = !player1Field.getText().isEmpty();
        boolean isPlayer2Set = !player2Field.getText().isEmpty();
        playButton.setDisabled(!(isPlayer1Set && isPlayer2Set));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1); // Set clear color to black
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(preGameScreenTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
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
        preGameScreenTexture.dispose();
        batch.dispose();
        stage.dispose();
    }
}
