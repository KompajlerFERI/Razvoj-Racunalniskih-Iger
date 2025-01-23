package si.um.feri.kompajler.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.kompajler.DigitalniDvojcek;
import si.um.feri.kompajler.config.GameConfig;
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
    private OrthographicCamera gameplayCamera;
    private Viewport gameplayViewport;
    private OrthographicCamera hudCamera;
    private Viewport hudViewport;
    private Stage hudStage;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    // Tiled map
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    private final AssetManager assetManager;

    private TextureAtlas gameplayAtlas;

    private Texture background;

    private BitmapFont playerNameFont;
    private Player player1;
    private Player player2;

    private String player1Username = "Taubek";
    private String player2Username = "Sluzek";
    private Label player1UsernameLabel;
    private Label player2UsernameLabel;

    private Label player1ScoreLabel;
    private Label player2ScoreLabel;

    private MapBoundsHandlerPlayer mapBoundsHandlerPlayer;
    private MapBoundsHandlerBullet mapBoundsHandlerBullet;

    public GameplayScreen(DigitalniDvojcek game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        // G A M E P L A Y
        gameplayCamera = new OrthographicCamera();
        gameplayViewport = new FitViewport(GameConfig.WORLD_UNITS_WIDTH, GameConfig.WORLD_UNITS_HEIGHT, gameplayCamera);

        tiledMap = new TmxMapLoader().load("map/tilemap.tmx");

        TiledMapTileLayer borders = (TiledMapTileLayer) tiledMap.getLayers().get("Borders");
        float tileWidthInWorldUnits = borders.getTileWidth() / GameConfig.WORLD_UNIT_PIXELS;
        float tileHeightInWorldUnits = borders.getTileHeight() / GameConfig.WORLD_UNIT_PIXELS;
        mapBoundsHandlerPlayer = new MapBoundsHandlerPlayer(borders, tileWidthInWorldUnits, tileHeightInWorldUnits);
        mapBoundsHandlerBullet = new MapBoundsHandlerBullet(borders, tileWidthInWorldUnits, tileHeightInWorldUnits);

        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / GameConfig.WORLD_UNIT_PIXELS);

        assetManager.load(AssetDescriptors.GAMEPLAY_ATLAS);
        assetManager.load(AssetDescriptors.SHOOT_WAV);
        assetManager.load(AssetDescriptors.EXPLOSION_WAV);
        assetManager.finishLoading();

        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY_ATLAS);
        background = assetManager.get(AssetDescriptors.GAMEPLAY_BACKGROUND);
        playerNameFont = assetManager.get(AssetDescriptors.PLAYER_NAME_FONT);

        player1 = new Player(gameplayAtlas, assetManager, 0);
        player2 = new Player(gameplayAtlas, assetManager, 1);
        GameManager.getInstance().players.add(player1);
        GameManager.getInstance().players.add(player2);

        GameManager.getInstance().playerScores.add(new PlayerScore(0, 0), new PlayerScore(1, 0));

        // H U D
        hudCamera = new OrthographicCamera();
        hudViewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT, hudCamera);

        hudStage = new Stage(hudViewport, game.getBatch());

        Image backgroundImage = new Image(background);

        Label.LabelStyle player1LabelStyle = new Label.LabelStyle(playerNameFont, new Color(0x00FA00FF));
        Label.LabelStyle player2LabelStyle = new Label.LabelStyle(playerNameFont, new Color(0xAB3227FF));
        Label.LabelStyle scoreLabelStyle = new Label.LabelStyle(playerNameFont, new Color(Color.WHITE));

        player1UsernameLabel = new Label(player1Username, player1LabelStyle);
        player2UsernameLabel = new Label(player2Username, player2LabelStyle);
        player1ScoreLabel = new Label("SCORE:0", scoreLabelStyle);
        player2ScoreLabel = new Label("SCORE:0", scoreLabelStyle);

        player1UsernameLabel.setPosition(112 - player1UsernameLabel.getWidth() / 2, 452 - player1UsernameLabel.getHeight() / 2);
        player2UsernameLabel.setPosition(912 - player2UsernameLabel.getWidth() / 2, 452 - player2UsernameLabel.getHeight() / 2);
        player1ScoreLabel.setPosition(112 - player1ScoreLabel.getWidth() / 2, 80 - player1ScoreLabel.getHeight() / 2);
        player2ScoreLabel.setPosition(912 - player2ScoreLabel.getWidth() / 2, 80 - player2ScoreLabel.getHeight() / 2);

        hudStage.addActor(backgroundImage);
        hudStage.addActor(player1UsernameLabel);
        hudStage.addActor(player2UsernameLabel);
        hudStage.addActor(player1ScoreLabel);
        hudStage.addActor(player2ScoreLabel);
    }

    @Override
    public void render(float delta) {
        update(delta);

        draw();
    }

    public void update(float delta) {
        System.out.println("WINNING: " + GameManager.getInstance().getWinningPlayer());
        if (GameManager.getInstance().getHighestPlayerScore() == 3) {
            int id = GameManager.getInstance().getWinningPlayer();
            GameManager.getInstance().resetPlayerScores();
            String winner = (id == 0) ? "GREEN" : "RED";
            GameManager.getInstance().winner = winner;

            // Clear bullets and players
            GameManager.getInstance().bullets.clear();
            GameManager.getInstance().players.clear();

            // Set screen to VictoryScreen
            game.setScreen(new VictoryScreen(game, winner));
            return;
        }

        for (Player player : GameManager.getInstance().players) {
            player.playerMovement(delta);
            mapBoundsHandlerPlayer.constrainPlayer(player);
        }

        for (Bullet bullet : GameManager.getInstance().getBullets()) {
            mapBoundsHandlerBullet.handleBulletCollision(bullet, delta);
        }

        GameManager.getInstance().updateBullets(delta, assetManager);

        for (PlayerScore playerScore : GameManager.getInstance().getPlayerScores()) {
            if (playerScore.getPlayerId() == 0) {
                player1ScoreLabel.setText("SCORE:" + String.valueOf(playerScore.getScore()));
                player1ScoreLabel.setPosition(112 - player1ScoreLabel.getWidth() / 2, 80 - player1ScoreLabel.getHeight() / 2);
            } else if (playerScore.getPlayerId() == 1) {
                player2ScoreLabel.setText("SCORE:" + String.valueOf(playerScore.getScore()));
                player2ScoreLabel.setPosition(912 - player2ScoreLabel.getWidth() / 2, 80 - player2ScoreLabel.getHeight() / 2);
            }
        }

        gameplayCamera.update();

        hudCamera.update();
    }

    public void draw() {
        ScreenUtils.clear(Color.BLACK);

        // O F F S E T   T O   C E N T E R   R E N D E R I N G
        Matrix4 offsetProjectionMatrix = new Matrix4(gameplayCamera.combined);
        offsetProjectionMatrix.translate(GameConfig.WORLD_UNITS_LEFT_OFFSET, 0, 0);

        tiledMapRenderer.setView(gameplayCamera);
        tiledMapRenderer.getBatch().setProjectionMatrix(offsetProjectionMatrix);
        tiledMapRenderer.render();

        game.getBatch().setProjectionMatrix(offsetProjectionMatrix);
        game.getBatch().begin();

        for (Player player : GameManager.getInstance().players) {
            game.getBatch().draw(player.getTankBottom(), player.rectangle.x, player.rectangle.y, player.rectangle.width / 2, player.rectangle.height / 2, player.rectangle.width, player.rectangle.height, 1, 1, player.getRotation());
        }

        for (Bullet bullet : GameManager.getInstance().getBullets()) {
            game.getBatch().setColor(bullet.getColor());
            game.getBatch().draw(bullet.getTextureRegion(), bullet.getBounds().x, bullet.getBounds().y, bullet.getBounds().width, bullet.getBounds().height);
            game.getBatch().setColor(Color.WHITE);
        }

        game.getBatch().end();

        // D R A W   H U D
        hudStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gameplayViewport.update(width, height);
        gameplayCamera.setToOrtho(false, gameplayViewport.getWorldWidth(), gameplayViewport.getWorldHeight());
        gameplayCamera.update();

        hudViewport.update(width, height);
        hudCamera.setToOrtho(false, hudViewport.getWorldWidth(), hudViewport.getWorldHeight());
        hudCamera.update();
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
