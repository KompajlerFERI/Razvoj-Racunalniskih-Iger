package si.um.feri.kompajler.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.badlogic.gdx.graphics.Color;

import org.json.JSONArray;
import org.json.JSONObject;

import si.um.feri.kompajler.DigitalniDvojcek;
import si.um.feri.kompajler.assets.AssetPaths;
import si.um.feri.kompajler.config.GameConfig;
import si.um.feri.kompajler.assets.AssetDescriptors;
import si.um.feri.kompajler.utils.ApiHelper;

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

    String jsonResponse = null;
    JSONArray menus = null;

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
        String id = restaurant.optString("id", "Unknown");
        String address = restaurant.optString("address", "No address available");
        float averageRating = restaurant.optFloat("averageRating", 0);

        // Working hours and tags data
        JSONArray workingHours = restaurant.optJSONArray("workingHours");
        JSONArray tags = restaurant.optJSONArray("tags");

        String workingHoursTitle = "Working Hours:";
        StringBuilder workingHoursText = new StringBuilder();
        if (workingHours != null) {
            for (int i = 0; i < workingHours.length(); i++) {
                JSONObject day = workingHours.getJSONObject(i);
                if (i == workingHours.length() - 1) {
                    workingHoursText.append("      ").append(day.optString("day")).append(": ")
                        .append(day.optString("open")).append(" - ")
                        .append(day.optString("close"));
                } else {
                    workingHoursText.append("      ").append(day.optString("day")).append(": ")
                        .append(day.optString("open")).append(" - ")
                        .append(day.optString("close")).append("\n");
                }
            }
        }

        StringBuilder tagsText = new StringBuilder("Tags:\n      ");
        if (tags != null) {
            for (int i = 0; i < tags.length(); i++) {
                tagsText.append(tags.getJSONObject(i).optString("name"));
                if (i < tags.length() - 1) {
                    if ((i + 1) % 3 == 0) {
                        tagsText.append("\n      ");
                    } else {
                        tagsText.append(", ");
                    }
                }
            }
        }

        // Fetch menu data from the API
        jsonResponse = ApiHelper.makeGetRequest(AssetPaths.URL + "menus");
        if (jsonResponse != null) {
            menus = new JSONArray(jsonResponse);
        } else {
            System.out.println("Failed to fetch data from the API.");
        }

        StringBuilder menuDetails = new StringBuilder("Menus:\n       ");
        menus = new JSONArray(jsonResponse);

        for (int i = 0; i < menus.length(); i++) {
            JSONObject menu = menus.getJSONObject(i);
            if (menu.optString("restaurant").equals(id)) {
                String dish = menu.optString("dish");
                JSONArray sideDishes = menu.optJSONArray("sideDishes");
                StringBuilder sides = new StringBuilder();
                for (int j = 0; j < sideDishes.length(); j++) {
                    sides.append(sideDishes.getString(j));
                    if (j < sideDishes.length() - 1) sides.append(", \n          ");
                }
                menuDetails.append(String.format("%s\n          Side Dishes: %s\n       ", dish, sides.toString()));
            }
        }


        Label.LabelStyle labelStyle = new Label.LabelStyle(game.assetManager.get(AssetDescriptors.SS_TEXT), Color.WHITE);

        // Display menu details in a UI Label inside the `InfoScreen`
        Label menuLabel = new Label(menuDetails.toString(), skin);
        menuLabel.setColor(Color.WHITE);
        window = new Window("Restaurant Menus", skin);
        window.add(menuLabel).pad(10).expand().fill();
        window.pack();
        window.setPosition((Gdx.graphics.getWidth() - window.getWidth()) / 2, (Gdx.graphics.getHeight() - window.getHeight()) / 2);

        Table table = new Table();
        table.defaults().pad(5 * camera.zoom);
        labelStyle.font.getData().setScale(0.6f);
        table.add(new Label(name, labelStyle)).left().row();
        labelStyle.font.getData().setScale(0.4f);
        table.add(new Label("   Address: " + address, labelStyle)).left().row();
        table.add(new Label("   Rating: " + averageRating, labelStyle)).left().row();
        table.add(new Label("   " + workingHoursTitle, labelStyle)).left().row();
        table.add(new Label(workingHoursText.toString(), labelStyle)).left().row();
        table.add(new Label("   " + tagsText.toString(), labelStyle)).left().row();
        table.add(new Label("   " + menuDetails.toString(), labelStyle)).left().row();

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
