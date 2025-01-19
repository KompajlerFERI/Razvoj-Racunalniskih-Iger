package main.java.si.um.feri.kompajler.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import org.json.JSONArray;
import org.json.JSONObject;

import main.java.si.um.feri.kompajler.DigitalniDvojcek;
import main.java.si.um.feri.kompajler.config.GameConfig;
import main.java.si.um.feri.kompajler.assets.AssetDescriptors;

public class InfoScreen implements Screen {
    private final DigitalniDvojcek game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Viewport viewport;
    private Stage stage;
    private Skin skin;
    private JSONObject restaurant;
    MapScreen mapFromBefore;

    private Window window;

    public InfoScreen(DigitalniDvojcek game, JSONObject restaurant, MapScreen mapFromBefore) {
        this.game = game;
        this.restaurant = restaurant;
        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        this.mapFromBefore = mapFromBefore;
    }

    @Override
    public void show() {
        // Set up viewport, camera, and stage
        viewport = new FitViewport(800, 800, camera);
        camera.setToOrtho(false, GameConfig.getWidth(), GameConfig.getHeight());
        camera.viewportWidth = GameConfig.getWidth();
        camera.viewportHeight = GameConfig.getHeight();
        camera.update();

        skin = game.assetManager.get(AssetDescriptors.UI_SKIN);
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

        // Prepare restaurant data
        String name = restaurant.optString("name", "Unknown");
        String address = restaurant.optString("address", "No address available");
        float averageRating = restaurant.optFloat("averageRating", 0);

        // Working hours and tags data
        JSONArray workingHours = restaurant.optJSONArray("workingHours");
        JSONArray tags = restaurant.optJSONArray("tags");

        StringBuilder workingHoursText = new StringBuilder("Working Hours:\n");
        if (workingHours != null) {
            for (int i = 0; i < workingHours.length(); i++) {
                JSONObject day = workingHours.getJSONObject(i);
                workingHoursText.append(day.optString("day")).append(": ")
                    .append(day.optString("open")).append(" - ")
                    .append(day.optString("close")).append("\n");
            }
        }

        StringBuilder tagsText = new StringBuilder("Tags:\n");
        if (tags != null) {
            for (int i = 0; i < tags.length(); i++) {
                tagsText.append(tags.getJSONObject(i).optString("name")).append("\n");
            }
        }

        // Create content table for restaurant info
        Table table = new Table();
        table.defaults().pad(5 * camera.zoom);
        table.add(new Label(name, skin)).row();
        table.add(new Label("Address: " + address, skin)).row();
        table.add(new Label("Rating: " + averageRating, skin)).row();
        table.add(new Label(workingHoursText.toString(), skin)).row();
        table.add(new Label(tagsText.toString(), skin)).row();

        // Set up scrollable window
        ScrollPane scrollPane = new ScrollPane(table, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(false, false);
        scrollPane.setScrollbarsVisible(true);

        window = new Window("Restaurant Info", skin);
        window.setSize(GameConfig.getWidth(), GameConfig.getHeight());
        window.setPosition(0, 0);
        window.add(scrollPane).fill().expand();

        // Back button to return to MapScreen
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                MapScreen mapScreen = new MapScreen(game, mapFromBefore);
                mapScreen.fromBefore = true;
                game.setScreen(new MapScreen(game, mapFromBefore));
            }
        });

        // Add back button to stage
        window.add(backButton).bottom().left().pad(10);
        stage.addActor(window);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
        stage.dispose();
    }
}
