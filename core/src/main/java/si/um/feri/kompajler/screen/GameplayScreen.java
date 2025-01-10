package si.um.feri.kompajler.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.kompajler.DigitalniDvojcek;
import si.um.feri.kompajler.assets.AssetDescriptors;
import si.um.feri.kompajler.config.GameConfig;
import si.um.feri.kompajler.gameplay.Bullet;
import si.um.feri.kompajler.gameplay.GameManager;
import si.um.feri.kompajler.gameplay.MapBoundsHandlerBullet;
import si.um.feri.kompajler.gameplay.MapBoundsHandlerPlayer;
import si.um.feri.kompajler.gameplay.Player;

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

    private final AssetManager assetManager;

    private TextureAtlas gameplayAtlas;
    private Player player1;
    private Player player2;

    private MapBoundsHandlerPlayer mapBoundsHandlerPlayer;
    private MapBoundsHandlerBullet mapBoundsHandlerBullet;

    public GameplayScreen(DigitalniDvojcek game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        gameplayViewport = new FitViewport(GameConfig.WIDTH, GameConfig.HEIGHT);
        stage = new Stage(gameplayViewport, game.getBatch());

        tiledMap = new TmxMapLoader().load("map/projekt-map.tmx");
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("Background");
        float mapWidth = layer.getWidth() * layer.getTileWidth();
        float mapHeight = layer.getHeight() * layer.getTileHeight();

        TiledMapTileLayer borders = (TiledMapTileLayer) tiledMap.getLayers().get("Borders");
        mapBoundsHandlerPlayer = new MapBoundsHandlerPlayer(borders);
        mapBoundsHandlerBullet = new MapBoundsHandlerBullet(borders);

        gameplayViewport = new FitViewport(mapWidth, mapHeight);
        stage = new Stage(gameplayViewport, game.getBatch());

        gameplayCamera = new OrthographicCamera();
        gameplayCamera.setToOrtho(false, mapWidth, mapHeight + 100);
        gameplayCamera.update();

        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        tiledMapRenderer.setView(gameplayCamera);

        shapeRenderer = new ShapeRenderer();


        assetManager.load(AssetDescriptors.GAMEPLAY_ATLAS);
        assetManager.finishLoading();

        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY_ATLAS);

        player1 = new Player(gameplayAtlas, 0);
    }

    @Override
    public void render(float delta) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        player1.playerMovement(deltaTime);

        mapBoundsHandlerPlayer.constrainPlayer(player1);

        GameManager.getInstance().updateBullets(deltaTime);

        ScreenUtils.clear(1f, 1f, 1f, 1f);

        gameplayCamera.update();
        tiledMapRenderer.setView(gameplayCamera);
        tiledMapRenderer.render();

        // To je za gori nad mapo score
        shapeRenderer.setProjectionMatrix(gameplayCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rect(0, gameplayCamera.viewportHeight - 100, GameConfig.WIDTH * 4, 100);
        shapeRenderer.end();

        // player
        game.getBatch().setProjectionMatrix(gameplayCamera.combined);
        game.getBatch().begin();
        game.getBatch().draw(player1.getTankBottom(), player1.rectangle.x, player1.rectangle.y, player1.rectangle.width / 2, player1.rectangle.height / 2, player1.rectangle.width, player1.rectangle.height, 1, 1, player1.getRotation());
        game.getBatch().draw(player1.getTankTop(), player1.rectangle.x, player1.rectangle.y, player1.rectangle.width / 2, player1.rectangle.height / 2, player1.rectangle.width, player1.rectangle.height, 1, 1, player1.getRotation());
        game.getBatch().end();

        game.getBatch().begin();
        for (Bullet bullet : GameManager.getInstance().getBullets()) {
            mapBoundsHandlerBullet.handleBulletCollision(bullet, deltaTime);
            game.getBatch().draw(bullet.getTextureRegion(), bullet.getBounds().x, bullet.getBounds().y, bullet.getBounds().width, bullet.getBounds().height);
        }
        game.getBatch().end();
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
