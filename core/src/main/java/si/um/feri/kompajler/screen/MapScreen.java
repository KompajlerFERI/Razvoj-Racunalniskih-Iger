package si.um.feri.kompajler.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.json.JSONArray;
import org.json.JSONObject;

import si.um.feri.kompajler.DigitalniDvojcek;
import si.um.feri.kompajler.assets.AssetDescriptors;
import si.um.feri.kompajler.assets.AssetPaths;
import si.um.feri.kompajler.utils.ApiHelper;
import si.um.feri.kompajler.utils.Constants;
import si.um.feri.kompajler.utils.Geolocation;
import si.um.feri.kompajler.utils.MapRasterTiles;
import si.um.feri.kompajler.utils.ZoomXY;

import java.io.IOException;


public class MapScreen implements Screen, GestureDetector.GestureListener {
    private final DigitalniDvojcek game;
    private ShapeRenderer shapeRenderer;
    private Vector3 touchPosition;
    private Stage stage;

    public MapScreen mapFromBefore;
    public boolean fromBefore = false;

    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Viewport viewport;
    private AssetManager assetManager;
    private TextureAtlas gameAtlas;

    private Texture[] mapTiles;
    private ZoomXY beginTile;   // top left tile

    // center geolocation
    private final Geolocation CENTER_GEOLOCATION = new Geolocation(46.545195, 15.644645);

    String jsonResponse = null;
    JSONArray restaurants = null;
    Vector2 locationPlaceholder = null;

    Texture texture_normal, texture_vegeterean, texture_pizza, texture_pizzanvegeterean;
    Skin skin;
    InfoScreen infoScreen;
    Table table;

    private JSONObject selectedRestaurant = null;
    private Vector2 selectedRestaurantPosition = null;

    boolean hasMeatTag = false,
        hasMixedTag = false,
        hasvegetereanTag = false,
        hasSaladTag = false,
        hasSeaFoodTag = false,
        hasPizzaTag = false,
        hasFastFoodTag = false,
        hasCeliacFriendlyFoodTag = false,
        allTagsFalse = true;
    boolean hasPizzaTagForRender = false,
        hasvegetereanTagForRender = false;


    public MapScreen(DigitalniDvojcek game, MapScreen mapFromBefore) {
        this.game = game;
        this.mapFromBefore = mapFromBefore;
    }

    @Override
    public void show() {
        if (mapFromBefore == null) {
            shapeRenderer = new ShapeRenderer();
            batch = new SpriteBatch();
            this.camera = new OrthographicCamera();
            viewport = new FitViewport(800, 800, camera);
            camera.viewportWidth = Constants.MAP_WIDTH / 2f;
            camera.viewportHeight = Constants.MAP_HEIGHT / 2f;
            camera.setToOrtho(false, Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
            camera.position.set(Constants.MAP_WIDTH / 2f, Constants.MAP_HEIGHT / 2f, 0);
            camera.zoom = 2f;
        }
        else {
            this.camera = mapFromBefore.camera;
            camera.update();
            this.viewport = mapFromBefore.viewport;
            this.batch = mapFromBefore.batch;
            this.shapeRenderer = mapFromBefore.shapeRenderer;
            this.touchPosition = mapFromBefore.touchPosition;
            this.tiledMap = mapFromBefore.tiledMap;
            this.tiledMapRenderer = mapFromBefore.tiledMapRenderer;
            this.mapTiles = mapFromBefore.mapTiles;
            this.beginTile = mapFromBefore.beginTile;
            this.jsonResponse = mapFromBefore.jsonResponse;
            this.restaurants = mapFromBefore.restaurants;
            this.locationPlaceholder = mapFromBefore.locationPlaceholder;
            this.texture_normal = mapFromBefore.texture_normal;
            this.texture_vegeterean = mapFromBefore.texture_vegeterean;
            this.texture_pizza = mapFromBefore.texture_pizza;
            this.skin = mapFromBefore.skin;
            this.infoScreen = mapFromBefore.infoScreen;
            this.selectedRestaurant = mapFromBefore.selectedRestaurant;
            this.selectedRestaurantPosition = mapFromBefore.selectedRestaurantPosition;
        }
        camera.update();
        assetManager = game.assetManager;
        skin = assetManager.get(AssetDescriptors.UI_SKIN);

        stage = new Stage(viewport, batch);
        infoScreen = new InfoScreen(game, selectedRestaurant, this);

        touchPosition = new Vector3();
        // vem da naj bi z asset managerjem delal
        texture_normal = new Texture("map_screen/pin_normal_low_rez.png");
        texture_vegeterean = new Texture("map_screen/pin_vegan_low_rez.png");
        texture_pizza = new Texture("map_screen/pin_pizza_low_rez.png");
        texture_pizzanvegeterean = new Texture("map_screen/pin_pizzanvegan_low_rez.png");

        try {
            //in most cases, geolocation won't be in the center of the tile because tile borders are predetermined (geolocation can be at the corner of a tile)
            ZoomXY centerTile = MapRasterTiles.getTileNumber(CENTER_GEOLOCATION.lat, CENTER_GEOLOCATION.lng, Constants.ZOOM);
            mapTiles = MapRasterTiles.getRasterTileZone(centerTile, Constants.NUM_TILES);
            //you need the beginning tile (tile on the top left corner) to convert geolocation to a location in pixels.
            beginTile = new ZoomXY(Constants.ZOOM, centerTile.x - ((Constants.NUM_TILES - 1) / 2), centerTile.y - ((Constants.NUM_TILES - 1) / 2));
        } catch (IOException e) {
            e.printStackTrace();
        }

        tiledMap = new TiledMap();
        MapLayers layers = tiledMap.getLayers();

        TiledMapTileLayer layer = new TiledMapTileLayer(Constants.NUM_TILES, Constants.NUM_TILES, MapRasterTiles.TILE_SIZE, MapRasterTiles.TILE_SIZE);
        int index = 0;
        for (int j = Constants.NUM_TILES - 1; j >= 0; j--) {
            for (int i = 0; i < Constants.NUM_TILES; i++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new StaticTiledMapTile(new TextureRegion(mapTiles[index], MapRasterTiles.TILE_SIZE, MapRasterTiles.TILE_SIZE)));
                layer.setCell(i, j, cell);
                index++;
            }
        }
        layers.add(layer);

        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        try {
            jsonResponse = ApiHelper.makeGetRequest(AssetPaths.URL + "restaurants");
            if (jsonResponse != null) {
                restaurants = new JSONArray(jsonResponse);
            } else {
                System.out.println("Failed to fetch data from the API.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }




        Window window = new Window("Filter", skin);
        float windowWidth = 200 * camera.zoom;
        float windowHeight = 200 * camera.zoom;
        window.setSize(windowWidth, windowHeight);
        window.setPosition(0, 0);
        window.setMovable(false);
        window.setResizable(false);

        table = createTableWithButtons();

        window.add(table);

        stage.addActor(window);
        //stage.addActor(table);

        GestureDetector gestureDetector = new GestureDetector(this);
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(gestureDetector);
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        handleInput();
        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        for (int i = 0; i < restaurants.length(); i++) {
            JSONObject restaurant = restaurants.getJSONObject(i);
            JSONObject location = restaurant.getJSONObject("location");
            JSONArray coordinates = location.getJSONArray("coordinates");
            double latitude = coordinates.getDouble(1);  // Index 1 for latitude
            double longitude = coordinates.getDouble(0); // Index 0 for longitude

            hasPizzaTagForRender = false;
            hasvegetereanTagForRender = false;
            JSONArray tags = restaurant.getJSONArray("tags");
            for (int j = 0; j < tags.length(); j++) {
                String tagName = tags.getJSONObject(j).getString("name");
                if (tagName.equalsIgnoreCase("vegetarijansko")) {
                    hasPizzaTagForRender = true;
                }
                if (tagName.equalsIgnoreCase("pizza")) {
                    hasvegetereanTagForRender = true;
                }
            }

            locationPlaceholder = MapRasterTiles.getPixelPosition(latitude, longitude, beginTile.x, beginTile.y);
            if (
                allTagsFalse
                || (hasMeatTag && tags.toString().contains("meso"))
                || (hasMixedTag && tags.toString().contains("mešano"))
                || (hasvegetereanTag && tags.toString().contains("vegetarijansko"))
                || (hasSaladTag && tags.toString().contains("solata"))
                || (hasSeaFoodTag && tags.toString().contains("morski-sadeži"))
                || (hasPizzaTag && tags.toString().contains("pizza"))
                || (hasFastFoodTag && tags.toString().contains("hitra-hrana"))
                || (hasCeliacFriendlyFoodTag && tags.toString().contains("celiakiji-prijazni-obroki"))
            ) {
                drawMarkers(camera, batch, locationPlaceholder, hasPizzaTagForRender, hasvegetereanTagForRender);
            }

            stage.act(delta);
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // No specific action needed for now
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    private void drawMarkers(OrthographicCamera camera, SpriteBatch spriteBatch, Vector2 marker, boolean isvegeterean, boolean isPizza) {
        // Use a SpriteBatch for drawing images
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        // Choose the appropriate texture based on tags
        Texture texture;
        if (isPizza && isvegeterean) {
            texture = texture_pizzanvegeterean;
        } else if (isvegeterean) {
            texture = texture_vegeterean;
        } else if (isPizza) {
            texture = texture_pizza;
        } else {
            texture = texture_normal;
        }

        // Draw the texture at the marker position
        float camZoom = camera.zoom; // Set a fixed size for the pin
        float pinWidth = camZoom * texture.getWidth() / 2 * 0.8f;
        float pinHeight = camZoom * texture.getHeight() / 2 * 0.8f;
        spriteBatch.draw(texture, marker.x - pinWidth / 2, marker.y, pinWidth, pinHeight);
        spriteBatch.end();
    }



    private Table createTableWithButtons() {
        Table newTable = new Table();
        newTable.top().left();
        newTable.padTop(52);
        newTable.setFillParent(true);

        Label meatLabel = new Label("Meso:", skin);
        TextButton meatButton = new TextButton("OFF", skin);
        meatButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hasMeatTag = !hasMeatTag;
                meatButton.setText(hasMeatTag ? "ON" : "OFF");
                checkIfAllTagsAreFalse();
            }
        });
        newTable.row().left();
        newTable.add(meatLabel).padRight(10);
        newTable.add(meatButton);

        Label mixedLabel = new Label("Mešano:", skin);
        TextButton mixedButton = new TextButton("OFF", skin);
        mixedButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hasMixedTag = !hasMixedTag;
                mixedButton.setText(hasMixedTag ? "ON" : "OFF");
                checkIfAllTagsAreFalse();
            }
        });
        newTable.row().left().padTop(10);
        newTable.add(mixedLabel).padRight(10);
        newTable.add(mixedButton);

        Label vegetereanLabel = new Label("Vegetarjansko:", skin);
        TextButton vegetereanButton = new TextButton("OFF", skin);
        vegetereanButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hasvegetereanTag = !hasvegetereanTag;
                vegetereanButton.setText(hasvegetereanTag ? "ON" : "OFF");
                checkIfAllTagsAreFalse();
            }
        });
        newTable.row().left().padTop(10);
        newTable.add(vegetereanLabel).padRight(10);
        newTable.add(vegetereanButton);

        Label saladLabel = new Label("Solata:", skin);
        TextButton saladButton = new TextButton("OFF", skin);
        saladButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hasSaladTag = !hasSaladTag;
                saladButton.setText(hasSaladTag ? "ON" : "OFF");
                checkIfAllTagsAreFalse();
            }
        });
        newTable.row().left().padTop(10);
        newTable.add(saladLabel).padRight(10);
        newTable.add(saladButton);

        Label seaFoodLabel = new Label("seaFood:", skin);
        TextButton seaFoodButton = new TextButton("OFF", skin);
        seaFoodButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hasPizzaTag = !hasPizzaTag;
                seaFoodButton.setText(hasPizzaTag ? "ON" : "OFF");
                checkIfAllTagsAreFalse();
            }
        });
        newTable.row().left().padTop(10);
        newTable.add(seaFoodLabel).padRight(10);
        newTable.add(seaFoodButton);

        Label pizzaLabel = new Label("pizza:", skin);
        TextButton pizzaButton = new TextButton("OFF", skin);
        pizzaButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hasPizzaTag = !hasPizzaTag;
                pizzaButton.setText(hasPizzaTag ? "ON" : "OFF");
                checkIfAllTagsAreFalse();
            }
        });
        newTable.row().left().padTop(10);
        newTable.add(pizzaLabel).padRight(10);
        newTable.add(pizzaButton);

        Label fastFoodLabel = new Label("fastFood:", skin);
        TextButton fastFoodButton = new TextButton("OFF", skin);
        fastFoodButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hasFastFoodTag = !hasFastFoodTag;
                fastFoodButton.setText(hasFastFoodTag ? "ON" : "OFF");
                checkIfAllTagsAreFalse();
            }
        });
        newTable.row().left().padTop(10);
        newTable.add(fastFoodLabel).padRight(10);
        newTable.add(fastFoodButton);

        Label celiakLabel = new Label("Celiakiji\nprijazni obroki:", skin);
        TextButton celiakButton = new TextButton("OFF", skin);
        celiakButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hasCeliacFriendlyFoodTag = !hasCeliacFriendlyFoodTag;
                celiakButton.setText(hasCeliacFriendlyFoodTag ? "ON" : "OFF");
                checkIfAllTagsAreFalse();
            }
        });
        newTable.row().left().padTop(10);
        newTable.add(celiakLabel).padRight(10);
        newTable.add(celiakButton);

        return newTable;
    }

    private void checkIfAllTagsAreFalse() {
        if (
            hasMeatTag
                || hasMixedTag
                || hasvegetereanTag
                || hasSaladTag
                || hasSeaFoodTag
                || hasPizzaTag
                || hasFastFoodTag
                || hasCeliacFriendlyFoodTag
        ) {
            allTagsFalse = false;
        } else {
            allTagsFalse = true;
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }


    // RETURNS IN WORLD UNITS
    @Override
    public boolean tap(float x, float y, int count, int button) {
        float[] worldCoords = convertToWorld(x, y, camera);
        float tapX = worldCoords[0];
        float tapY = worldCoords[1];

        float closestDistanceSquared = Float.MAX_VALUE;
        JSONObject closestRestaurant = null;
        Vector2 closestPinPosition = null;

        // Loop through all the restaurants and check the distance to the pin
        for (int i = 0; i < restaurants.length(); i++) {
            JSONObject restaurant = restaurants.getJSONObject(i);
            JSONObject location = restaurant.getJSONObject("location");
            JSONArray coordinates = location.getJSONArray("coordinates");
            double latitude = coordinates.getDouble(1);
            double longitude = coordinates.getDouble(0);

            // Get the position of the pin on the screen
            Vector2 pinPosition = MapRasterTiles.getPixelPosition(latitude, longitude, beginTile.x, beginTile.y);

            // Calculate the squared distance between the tapped position and the pin position
            float deltaX = tapX - pinPosition.x;
            float deltaY = tapY - pinPosition.y;
            float distanceSquared = deltaX * deltaX + deltaY * deltaY;

            // Adjust tolerance for the marker's clickable area based on zoom level
            float pinRadius = 16f * camera.zoom; // Scale the clickable radius based on zoom level
            float markerRadiusSquared = pinRadius * pinRadius;

            // If the tap is within the marker's clickable area, update the closest restaurant
            if (distanceSquared <= markerRadiusSquared && distanceSquared < closestDistanceSquared) {
                closestDistanceSquared = distanceSquared;
                closestRestaurant = restaurant;
                closestPinPosition = pinPosition;
            }
        }

        if (closestRestaurant != null && closestPinPosition != null) {
            selectedRestaurant = closestRestaurant;
            selectedRestaurantPosition = closestPinPosition;
            camera.update();
            game.setScreen(new InfoScreen(game, selectedRestaurant, this));
        }
        // else if (
        //     window != null
        //     && (tapX < window.getX()
        //     || tapX > window.getX() + window.getWidth()
        //     || tapY < window.getY()
        //     || tapY > window.getY() + window.getHeight())
        // ) {
        //     selectedRestaurant = null;
        //     selectedRestaurantPosition = null;
        // }

        return true;
    }

    private float[] convertToWorld(float screenX, float screenY, Camera camera) {
        Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
        return new float[]{worldCoords.x, worldCoords.y};
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        camera.translate(-deltaX, deltaY);
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        if (initialDistance >= distance)
            camera.zoom += 0.02;
        else
            camera.zoom -= 0.02;
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.zoom += 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom -= 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-9, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(9, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0, -9, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0, 9, 0);
        }

        if (!fromBefore) {
            camera.zoom = MathUtils.clamp(camera.zoom, 0.5f, 1.9f);

            float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
            float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

            camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f, Constants.MAP_WIDTH - effectiveViewportWidth / 2f);
            camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f, Constants.MAP_HEIGHT - effectiveViewportHeight / 2f);
        }
    }
}
