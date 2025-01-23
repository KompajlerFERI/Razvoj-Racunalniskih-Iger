package si.um.feri.kompajler.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.json.JSONArray;
import org.json.JSONObject;

import si.um.feri.kompajler.DigitalniDvojcek;
import si.um.feri.kompajler.assets.AssetDescriptors;
import si.um.feri.kompajler.assets.AssetPaths;
import si.um.feri.kompajler.config.GameConfig;
import si.um.feri.kompajler.utils.ApiHelper;
import si.um.feri.kompajler.utils.Constants;
import si.um.feri.kompajler.utils.Geolocation;
import si.um.feri.kompajler.utils.MapRasterTiles;
import si.um.feri.kompajler.utils.ZoomXY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


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

    private final Geolocation CENTER_GEOLOCATION = new Geolocation(46.548964, 15.62816);

    String jsonResponse = null;
    JSONArray restaurants = null;
    Vector2 locationPlaceholder = null;

    Texture texture_normal, texture_vegan, texture_pizza, texture_pizzanvegan, personTexture, restaurantDefaultTexture, pizzeriaTexture, fastFoodRestaurantTexture, exclamationTexture;

    Actor exclamationActor;
    Skin skin;
    Window window;
    InfoScreen infoScreen;

    private JSONObject selectedRestaurant = null;
    private Vector2 selectedRestaurantPosition = null;
    private boolean isPanning = false;
    private float startX, startY, startZoom;
    private float targetX, targetY, targetZoom;
    private float duration = 0.5f; // 2 seconds for the transition
    private long startTime;

    public MapScreen(DigitalniDvojcek game, MapScreen mapFromBefore) {
        this.game = game;
        this.mapFromBefore = mapFromBefore;

    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT, camera);
        camera.zoom = Constants.MAX_CAMERA_ZOOM;

        batch = game.getBatch();
        shapeRenderer = new ShapeRenderer();

        assetManager = game.assetManager;
        skin = assetManager.get(AssetDescriptors.UI_SKIN);

        stage = new Stage(viewport, batch);

        window = null;
        /*infoScreen = new InfoScreen(game, selectedRestaurant, this);*/

        GestureDetector gestureDetector = new GestureDetector(this);
        Gdx.input.setInputProcessor(gestureDetector);

        touchPosition = new Vector3();
        // vem da naj bi z asset managerjem delal
        texture_normal = new Texture("map_screen/pin_normal_low_rez.png");
        texture_vegan = new Texture("map_screen/pin_vegan_low_rez.png");
        texture_pizza = new Texture("map_screen/pin_pizza_low_rez.png");
        texture_pizzanvegan = new Texture("map_screen/pin_pizzanvegan_low_rez.png");
        personTexture = new Texture("map_screen/person-solid.png");
        restaurantDefaultTexture = new Texture("map_screen/restaurant_normal.png");
        pizzeriaTexture = new Texture("map_screen/restaurant_pizza.png");
        fastFoodRestaurantTexture = new Texture("map_screen/restaurant_burger.png");
        exclamationTexture = new Texture("map_screen/exclamation.png");

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
                sortRestaurantsByCoordinates();
            } else {
                System.out.println("Failed to fetch data from the API.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // T E M P
        for (int i = 0; i < restaurants.length(); i++) {
            JSONObject restaurant = restaurants.getJSONObject(i);
            JSONObject location = restaurant.getJSONObject("location");
            JSONArray coordinates = location.getJSONArray("coordinates");
            int lastCapacity = restaurant.getInt("lastCapacity");
            int maxCapacity = restaurant.getInt("maxCapacity");
            double latitude = coordinates.getDouble(1);  // Index 1 for latitude
            double longitude = coordinates.getDouble(0); // Index 0 for longitude

            Vector2 temp = MapRasterTiles.getPixelPosition(latitude, longitude, beginTile.x, beginTile.y);

            int numberOfWanderingPeople = (int) ((lastCapacity / (float) maxCapacity) * 10);

            for(int j = 0; j < numberOfWanderingPeople; j++) {
                stage.addActor(createWanderingPerson(temp.x, temp.y));
            }
        }
    }

    public void update(float delta) {
        handleInput();

        if (isPanning) {
            updateCamera(delta);
        }

        camera.update();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.WHITE);

        update(delta);

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        draw();

        if(camera.zoom <= 0.9f) {
            stage.act(delta);
            stage.draw();
        }
    }

    public void draw() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();


        if(camera.zoom <= 0.9f) {
            drawRestaurants();
        } else {
            drawMarkers();
        }

        batch.end();
    }

    public void drawMarkers() {
        for (int i = 0; i < restaurants.length(); i++) {
            JSONObject restaurant = restaurants.getJSONObject(i);
            JSONObject location = restaurant.getJSONObject("location");
            JSONArray coordinates = location.getJSONArray("coordinates");
            double latitude = coordinates.getDouble(1);  // Index 1 for latitude
            double longitude = coordinates.getDouble(0); // Index 0 for longitude

            boolean hasVeganTag = false;
            boolean hasPizzaTag = false;
            JSONArray tags = restaurant.getJSONArray("tags");
            for (int j = 0; j < tags.length(); j++) {
                String tagName = tags.getJSONObject(j).getString("name");
                if (tagName.equalsIgnoreCase("vegetarijansko")) {
                    hasVeganTag = true;
                }
                if (tagName.equalsIgnoreCase("pizza")) {
                    hasPizzaTag = true;
                }
            }

            locationPlaceholder = MapRasterTiles.getPixelPosition(latitude, longitude, beginTile.x, beginTile.y);
            drawMarker(camera, locationPlaceholder, hasVeganTag, hasPizzaTag);

            if (selectedRestaurant != null) {
                drawPopUpWindow(selectedRestaurant, selectedRestaurantPosition);
            }
            else {
                if (window != null) {
                    window.remove();
                    window = null;
                }
            }
            /*stage.act(delta);
            stage.draw();*/
        }
    }

    private void drawMarker(OrthographicCamera camera, Vector2 marker, boolean isVegan, boolean isPizza) {

        // Choose the appropriate texture based on tags
        Texture texture;
        if (isPizza && isVegan) {
            texture = texture_pizzanvegan;
        } else if (isVegan) {
            texture = texture_vegan;
        } else if (isPizza) {
            texture = texture_pizza;
        } else {
            texture = texture_normal;
        }

        // Draw the texture at the marker position
        float camZoom = camera.zoom; // Set a fixed size for the pin
        float pinWidth = camZoom * texture.getWidth() / 2 * 0.8f;
        float pinHeight = camZoom * texture.getHeight() / 2 * 0.8f;
        batch.draw(texture, marker.x - pinWidth / 2, marker.y, pinWidth, pinHeight);
    }

    public void drawRestaurants() {
        for (int i = 0; i < restaurants.length(); i++) {
            JSONObject restaurant = restaurants.getJSONObject(i);
            JSONObject location = restaurant.getJSONObject("location");
            JSONArray coordinates = location.getJSONArray("coordinates");
            double latitude = coordinates.getDouble(1);  // Index 1 for latitude
            double longitude = coordinates.getDouble(0); // Index 0 for longitude

            boolean hasPizzaTag = false;
            boolean hasFastFoodTag = false;
            JSONArray tags = restaurant.getJSONArray("tags");
            for (int j = 0; j < tags.length(); j++) {
                String tagName = tags.getJSONObject(j).getString("name");
                if (tagName.equalsIgnoreCase("pizza")) {
                    hasPizzaTag = true;
                }
                if (tagName.equalsIgnoreCase("hitra-hrana")) {
                    hasFastFoodTag = true;
                }
            }

            locationPlaceholder = MapRasterTiles.getPixelPosition(latitude, longitude, beginTile.x, beginTile.y);
            drawRestaurant(camera, locationPlaceholder, hasFastFoodTag, hasPizzaTag);

            if (selectedRestaurant != null) {
                drawPopUpWindow(selectedRestaurant, selectedRestaurantPosition);
            }
            else {
                if (window != null) {
                    window.remove();
                    window = null;
                }
            }
            /*stage.act(delta);
            stage.draw();*/
        }
    }

    private void drawRestaurant(OrthographicCamera camera, Vector2 marker, boolean isFastFood, boolean isPizza) {

        // Choose the appropriate texture based on tags
        Texture texture;
        if (isPizza) {
            texture = pizzeriaTexture;
        } else if (isFastFood) {
            texture = fastFoodRestaurantTexture;
        } else {
            texture = restaurantDefaultTexture;
        }

        // Draw the texture at the marker position
        float camZoom = camera.zoom; // Set a fixed size for the pin
        float pinWidth = camZoom * texture.getWidth() / 2 * 0.4f;
        float pinHeight = camZoom * texture.getHeight() / 2 * 0.4f;
        batch.draw(texture, marker.x - pinWidth / 2, marker.y, pinWidth, pinHeight);
    }



    private void drawPopUpWindow(JSONObject restaurant, Vector2 position) {
        float width = viewport.getScreenWidth();
        float height = viewport.getScreenHeight() / 2;

        String name = restaurant.optString("name", "Unknown");
        String address = restaurant.optString("address", "No address available");
        float averageRating = restaurant.optFloat("averageRating", 0);

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

        Table table = new Table();
        table.defaults().pad(5 * camera.zoom);
        table.add(new Label(name, skin)).row();
        table.add(new Label("Address: " + address, skin)).row();
        table.add(new Label("Rating: " + averageRating, skin)).row();
        table.add(new Label(workingHoursText.toString(), skin)).row();
        table.add(new Label(tagsText.toString(), skin)).row();

        ScrollPane scrollPane = new ScrollPane(table, skin);
        scrollPane.setFadeScrollBars(false); // Disable fade effect for scroll bars
        scrollPane.setScrollingDisabled(false, false); // Allow scrolling both horizontally and vertically if needed
        scrollPane.setScrollbarsVisible(true);

        float newX = position.x - width / 2;
        float newY = position.y - height - 5f;
        window = new Window("Restaurant Info", skin);
        window.setSize(width, height);
        window.setPosition(newX, newY);
        window.add(scrollPane).fill().expand();

        if (newX < 0) {
            camera.position.set(GameConfig.getWidth() / 2 * camera.zoom, position.y + 100f, 0);
            window.setPosition(GameConfig.getHeight() / 2 * camera.zoom - width / 2, newY);
        }
        else if (newX > GameConfig.getWidth()) {
            camera.position.set(position.x - width, position.y - 100f, 0);
            window.setPosition(position.x - width / 2, newY);
        }
        else {
            camera.position.set(position.x, position.y - 100f, 0);
        }
        if (newY < 0) {
            camera.position.set(GameConfig.getHeight() / 2 * camera.zoom, position.y + 100f, 0);
            window.setPosition(newX, position.y + 100f - height / 2);
        }
        else if (newY > GameConfig.getHeight()) {
            camera.position.set(camera.position.x, position.y - 100f, 0);
            window.setPosition(newX, position.y - 100f - height / 2);
        }
        else {
            camera.position.set(camera.position.x, position.y - 100f, 0);
        }

        camera.zoom = 0.5f;
        camera.update();
        stage.clear();
        stage.addActor(window);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        texture_normal.dispose();
        texture_vegan.dispose();
        texture_pizza.dispose();
        texture_pizzanvegan.dispose();
        personTexture.dispose();
        restaurantDefaultTexture.dispose();
        pizzeriaTexture.dispose();
        fastFoodRestaurantTexture.dispose();
        exclamationTexture.dispose();
    }

    @Override
    public void pause() {
        // No specific action needed for now
    }

    @Override
    public void resume() {
        // No specific action needed for now
    }

    @Override
    public void hide() {
        // No specific action needed for now
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        // If no window is open, proceed with normal touch handling
        if (window == null) {
            touchPosition.set(x, y, 0);
            camera.unproject(touchPosition);
        } else {
            // Convert the touch position to world coordinates
            float[] worldCoords = convertToWorld(x, y, camera);
            float dragX = worldCoords[0];
            float dragY = worldCoords[1];

            // Convert window's position to screen coordinates (in case it's in world space)
            camera.project(touchPosition.set(window.getX(), window.getY(), 0));
            float windowX = touchPosition.x;
            float windowY = touchPosition.y;

            // Check if the touch is inside the window bounds in screen space
            if (x >= windowX && x <= windowX + window.getWidth() && y >= windowY && y <= windowY + window.getHeight()) {
                // Touch is inside the window, return true to consume the event
                return true;
            }
        }

        return false;  // Allow other touchDown events to be processed
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
        camera.translate(-deltaX * camera.zoom, deltaY * camera.zoom);
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        if (initialDistance >= distance)
            camera.zoom += Constants.CAMERA_ZOOM_SPEED;
        else
            camera.zoom -= Constants.CAMERA_ZOOM_SPEED;
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
            camera.zoom = MathUtils.clamp(camera.zoom + Constants.CAMERA_ZOOM_SPEED, Constants.MIN_CAMERA_ZOOM, Constants.MAX_CAMERA_ZOOM);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom = MathUtils.clamp(camera.zoom - Constants.CAMERA_ZOOM_SPEED, Constants.MIN_CAMERA_ZOOM, Constants.MAX_CAMERA_ZOOM);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-Constants.CAMERA_MOVEMENT_SPEED * camera.zoom, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(Constants.CAMERA_MOVEMENT_SPEED * camera.zoom, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0, -Constants.CAMERA_MOVEMENT_SPEED * camera.zoom, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0, Constants.CAMERA_MOVEMENT_SPEED * camera.zoom, 0);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            priceChange();
        }

        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f, Constants.MAP_WIDTH - effectiveViewportWidth / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f, Constants.MAP_HEIGHT - effectiveViewportHeight / 2f);
    }

    private float[] convertToWorld(float screenX, float screenY, Camera camera) {
        Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
        return new float[]{worldCoords.x, worldCoords.y};
    }

    private Actor createWanderingPerson(float centerX, float centerY) {
        Image person = new Image(personTexture);
        person.setScale(0.1f);

        person.setPosition(getRandomPosition(centerX, centerY).x, getRandomPosition(centerX, centerY).y);
        person.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.moveTo(getRandomPosition(centerX, centerY).x, getRandomPosition(centerX, centerY).y, MathUtils.random(1f, 3f)),
                    Actions.delay(MathUtils.random(1f, 2f)),
                    Actions.moveTo(getRandomPosition(centerX, centerY).x, getRandomPosition(centerX, centerY).y, MathUtils.random(1f, 3f)),
                    Actions.delay(MathUtils.random(1f, 2f)),
                    Actions.moveTo(getRandomPosition(centerX, centerY).x, getRandomPosition(centerX, centerY).y, MathUtils.random(1f, 3f)),
                    Actions.delay(MathUtils.random(1f, 2f)),
                    Actions.moveTo(getRandomPosition(centerX, centerY).x, getRandomPosition(centerX, centerY).y, MathUtils.random(1f, 3f)),
                    Actions.delay(MathUtils.random(1f, 2f)),
                    Actions.moveTo(getRandomPosition(centerX, centerY).x, getRandomPosition(centerX, centerY).y, MathUtils.random(1f, 3f)),
                    Actions.delay(MathUtils.random(1f, 2f))
                )
            )
        );

        return person;
    }

    private Vector2 getRandomPosition(float centerX, float centerY) {
        float radius = 20; // Define the radius
        float angle = MathUtils.random(0, 2 * MathUtils.PI); // Random angle
        float distance = MathUtils.random(0, radius); // Random distance within the radius
        float x = centerX + distance * MathUtils.cos(angle);
        float y = centerY + distance * MathUtils.sin(angle);
        return new Vector2(x, y);
    }

    public void sortRestaurantsByCoordinates() {
        List<JSONObject> restaurantList = new ArrayList<>();
        for (int i = 0; i < restaurants.length(); i++) {
            restaurantList.add(restaurants.getJSONObject(i));
        }

        Collections.sort(restaurantList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject a, JSONObject b) {
                double latA = a.getJSONObject("location").getJSONArray("coordinates").getDouble(1);
                double lonA = a.getJSONObject("location").getJSONArray("coordinates").getDouble(0);
                double latB = b.getJSONObject("location").getJSONArray("coordinates").getDouble(1);
                double lonB = b.getJSONObject("location").getJSONArray("coordinates").getDouble(0);

                if (latA != latB) {
                    return Double.compare(latB, latA); // Sort by latitude descending
                } else {
                    return Double.compare(lonA, lonB); // Sort by longitude ascending
                }
            }
        });

        restaurants = new JSONArray(restaurantList);
    }

    private void priceChange() {
        if (restaurants != null && restaurants.length() > 0) {
            JSONObject restaurant = restaurants.getJSONObject(24); // Pan to the first restaurant
            double lat = restaurant.getJSONObject("location").getJSONArray("coordinates").getDouble(1);
            double lon = restaurant.getJSONObject("location").getJSONArray("coordinates").getDouble(0);

            // Convert lat/lon to world coordinates
            Vector2 targetPosition = MapRasterTiles.getPixelPosition(lat, lon, beginTile.x, beginTile.y);

            startPanningTo(targetPosition.x, targetPosition.y, 0.6f);
            stage.addActor(createExclamation(targetPosition.x - 10, targetPosition.y + 30));
        }
    }

    private void startPanningTo(float x, float y, float zoom) {
        startX = camera.position.x;
        startY = camera.position.y;
        startZoom = camera.zoom;
        targetX = x;
        targetY = y;
        targetZoom = zoom;
        startTime = TimeUtils.millis();
        isPanning = true;
    }

    private Actor createExclamation(float x, float y) {
        Image exclamationImage = new Image(exclamationTexture);
        exclamationImage.setPosition(x, y);
        exclamationImage.setScale(0.3f);
        exclamationImage.setVisible(false);

        exclamationImage.addAction(
            Actions.sequence(
                Actions.delay(duration),
                Actions.show(),
                Actions.sequence(
                    Actions.moveBy(2, 0, 0.1f), // Move right
                    Actions.moveBy(-4, 0, 0.1f), // Move left
                    Actions.moveBy(4, 0, 0.1f), // Move right
                    Actions.moveBy(-2, 0, 0.1f),  // Move left
                    Actions.moveBy(1, 0, 0.1f), // Move right
                    Actions.moveBy(-1, 0, 0.1f)  // Move left
                ),
                Actions.delay(0.5f),
                Actions.removeActor()
            )
        );

        return exclamationImage;
    }

    private void updateCamera(float delta) {
        float elapsed = (TimeUtils.millis() - startTime) / 1000f;
        float progress = Math.min(elapsed / duration, 1f); // Clamp progress to 1

        // Interpolate position and zoom
        camera.position.x = startX + (targetX - startX) * progress;
        camera.position.y = startY + (targetY - startY) * progress;
        camera.zoom = startZoom + (targetZoom - startZoom) * progress;

        camera.update();

        // Stop panning when the animation is complete
        if (progress >= 1f) {
            isPanning = false;
        }
    }
}
