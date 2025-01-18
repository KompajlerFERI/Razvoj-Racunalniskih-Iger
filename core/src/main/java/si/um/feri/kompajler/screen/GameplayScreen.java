package si.um.feri.kompajler.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
import si.um.feri.kompajler.gameplay.PlayerScore;

public class GameplayScreen implements Screen {
    private final DigitalniDvojcek game;
    private Viewport gameplayViewport;
    private Viewport hudViewport;
    private Stage stage;
    private OrthographicCamera gameplayCamera;
    private OrthographicCamera hudCamera;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private GlyphLayout layout;

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
        font = new BitmapFont();
        font.getData().setScale(5); // Scale the font size by 10
        layout = new GlyphLayout();

        assetManager.load(AssetDescriptors.GAMEPLAY_ATLAS);
        assetManager.load(AssetDescriptors.SHOOT_WAV);
        assetManager.load(AssetDescriptors.EXPLOSION_WAV);
        assetManager.finishLoading();

        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY_ATLAS);

        player1 = new Player(gameplayAtlas, assetManager, 0);
        player2 = new Player(gameplayAtlas, assetManager, 1);
        GameManager.getInstance().players.add(player1);
        GameManager.getInstance().players.add(player2);

        GameManager.getInstance().playerScores.add(new PlayerScore(0, 0), new PlayerScore(1, 0));
    }

    @Override
    public void render(float delta) {
        float deltaTime = Gdx.graphics.getDeltaTime();

        if (GameManager.getInstance().getHighestPlayerScore() == 7) {
            GameManager.getInstance().resetPlayerScores();
            int id = GameManager.getInstance().getWinningPlayer();
            GameManager.getInstance().winner = (id == 0) ? 1 : 0;

            // Clear bullets and players
            GameManager.getInstance().bullets.clear();
            GameManager.getInstance().players.clear();

            // Set screen to VictoryScreen
            game.setScreen(new VictoryScreen(game, GameManager.getInstance().winner));
            return;
        }

        for (Player player : GameManager.getInstance().players) {
            player.playerMovement(deltaTime);
            mapBoundsHandlerPlayer.constrainPlayer(player);
        }

        GameManager.getInstance().updateBullets(deltaTime, assetManager);

        ScreenUtils.clear(1f, 1f, 1f, 1f);

        gameplayCamera.update();
        tiledMapRenderer.setView(gameplayCamera);
        tiledMapRenderer.render();

        shapeRenderer.setProjectionMatrix(gameplayCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rect(0, gameplayCamera.viewportHeight - 100, GameConfig.WIDTH * 4, 100);
        shapeRenderer.end();

        game.getBatch().setProjectionMatrix(gameplayCamera.combined);
        game.getBatch().begin();

        for (Player player : GameManager.getInstance().players) {
            game.getBatch().draw(player.getTankBottom(), player.rectangle.x, player.rectangle.y, player.rectangle.width / 2, player.rectangle.height / 2, player.rectangle.width, player.rectangle.height, 1, 1, player.getRotation());
        }

        for (Bullet bullet : GameManager.getInstance().getBullets()) {
            mapBoundsHandlerBullet.handleBulletCollision(bullet, deltaTime);
            game.getBatch().setColor(bullet.getColor());
            game.getBatch().draw(bullet.getTextureRegion(), bullet.getBounds().x, bullet.getBounds().y, bullet.getBounds().width, bullet.getBounds().height);
            game.getBatch().setColor(Color.WHITE);
        }

        // Draw scores
        for (PlayerScore playerScore : GameManager.getInstance().getPlayerScores()) {
            if (playerScore.getPlayerId() == 0) {
                layout.setText(font, "Green : " + playerScore.getScore());
                font.setColor(Color.RED);
                font.draw(game.getBatch(), layout, 10, gameplayCamera.viewportHeight - 10);
            } else if (playerScore.getPlayerId() == 1) {
                layout.setText(font, "Red: " + playerScore.getScore());
                font.setColor(Color.GREEN);
                font.draw(game.getBatch(), layout, gameplayCamera.viewportWidth - layout.width - 10, gameplayCamera.viewportHeight - 10);
            }
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

    public AssetManager getAssetManager() {
        return assetManager;
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
        font.dispose();
    }
}
