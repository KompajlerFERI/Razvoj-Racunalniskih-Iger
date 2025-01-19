package main.java.si.um.feri.kompajler.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import main.java.si.um.feri.kompajler.DigitalniDvojcek;
import main.java.si.um.feri.kompajler.config.GameConfig;

public class GameplayScreen implements Screen {
    private final DigitalniDvojcek game;
    private Viewport gameplayViewport;
    private Viewport hudViewport;
    private Stage stage;
    private OrthographicCamera gameplayCamera;
    private OrthographicCamera hudCamera;
    private ShapeRenderer shapeRenderer;

    // Tiled map
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    public GameplayScreen(DigitalniDvojcek game) {
        this.game = game;
    }

    @Override
    public void show() {
        gameplayViewport = new FitViewport(GameConfig.getWidth(), GameConfig.getHeight());
        stage = new Stage(gameplayViewport, game.getBatch());

        tiledMap = new TmxMapLoader().load("map/projekt-map.tmx");
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("Background");
        float mapWidth = layer.getWidth() * layer.getTileWidth();
        float mapHeight = layer.getHeight() * layer.getTileHeight();

        gameplayViewport = new FitViewport(mapWidth, mapHeight);
        stage = new Stage(gameplayViewport, game.getBatch());

        gameplayCamera = new OrthographicCamera();
        gameplayCamera.setToOrtho(false, mapWidth, mapHeight + 100);
        gameplayCamera.update();

        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        tiledMapRenderer.setView(gameplayCamera);

        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(1f, 1f, 1f, 1f);

        gameplayCamera.update();
        tiledMapRenderer.setView(gameplayCamera);
        tiledMapRenderer.render();

        // Draw black rectangle
        shapeRenderer.setProjectionMatrix(gameplayCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rect(0, gameplayCamera.viewportHeight - 100, GameConfig.getWidth() * 4, 100);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        gameplayViewport.update(width, height);
        gameplayCamera.setToOrtho(false, gameplayViewport.getWorldWidth(), gameplayViewport.getWorldHeight() + 100);
        gameplayCamera.update();
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
        shapeRenderer.dispose();
    }
}
