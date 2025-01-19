package main.java.si.um.feri.kompajler.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.json.JSONArray;
import org.json.JSONObject;

import main.java.si.um.feri.kompajler.DigitalniDvojcek;
import main.java.si.um.feri.kompajler.utils.ApiHelper;
import main.java.si.um.feri.kompajler.utils.Constants;
import main.java.si.um.feri.kompajler.utils.Geolocation;
import main.java.si.um.feri.kompajler.utils.MapRasterTiles;
import main.java.si.um.feri.kompajler.utils.ZoomXY;

import java.io.IOException;


public class MapScreen implements Screen, GestureDetector.GestureListener {
    private final DigitalniDvojcek game;
    private ShapeRenderer shapeRenderer;
    private Vector3 touchPosition;

    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Viewport viewport;

    private Texture[] mapTiles;
    private ZoomXY beginTile;   // top left tile

    // center geolocation
    private final Geolocation CENTER_GEOLOCATION = new Geolocation(46.545195, 15.644645);


    String jsonResponse = null;
    JSONArray restaurants = null;
    Vector2 locationPlaceholder = null;

    Texture texture_normal, texture_vegan, texture_pizza;

    public MapScreen(DigitalniDvojcek game) {
        this.game = game;
    }

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 800, camera);
        camera.setToOrtho(false, Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        camera.position.set(Constants.MAP_WIDTH / 2f, Constants.MAP_HEIGHT / 2f, 0);
        camera.viewportWidth = Constants.MAP_WIDTH / 2f;
        camera.viewportHeight = Constants.MAP_HEIGHT / 2f;
        camera.zoom = 2f;
        camera.update();

        GestureDetector gestureDetector = new GestureDetector(this);
        Gdx.input.setInputProcessor(gestureDetector);

        touchPosition = new Vector3();
        texture_normal = new Texture("map_screen/pin_normal_low_rez.png");
        texture_vegan = new Texture("map_screen/pin_vegan_low_rez.png");
        texture_pizza = new Texture("map_screen/pin_pizza_low_rez.png");

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
            jsonResponse = ApiHelper.makeGetRequest(ApiHelper.url + "restaurants");
            if (jsonResponse != null) {
                restaurants = new JSONArray(jsonResponse);
            } else {
                System.out.println("Failed to fetch data from the API.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            drawMarkers(camera, batch, locationPlaceholder, hasVeganTag, hasPizzaTag);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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

    private void drawMarkers(OrthographicCamera camera, SpriteBatch spriteBatch, Vector2 marker, boolean isVegan, boolean isPizza) {
        // Use a SpriteBatch for drawing images
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        // Choose the appropriate texture based on tags
        Texture texture;
        if (isPizza) {
            texture = texture_normal;
        } else if (isVegan) {
            texture = texture_vegan;
        } else {
            texture = texture_pizza;
        }

        // Draw the texture at the marker position
        float camZoom = camera.zoom; // Set a fixed size for the pin
        float pinWidth = camZoom * texture.getWidth() / 2;
        float pinHeight = camZoom * texture.getHeight() / 2;
        spriteBatch.draw(texture, marker.x - pinWidth / 2, marker.y, pinWidth, pinHeight);
        spriteBatch.end();
    }

    private void drawMarkers(Vector2 marker) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(marker.x, marker.y, 10);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        touchPosition.set(x, y, 0);
        camera.unproject(touchPosition);
        return false;
    }

    // RETURNS IN WORLD UNITS
    @Override
    public boolean tap(float x, float y, int count, int button) {
        float[] worldCoords = convertToWorld(x, y, camera);
        System.out.println("Tapped at: " + GameConfig.scaleToWoldUnits(worldCoords[0]) + ", " + worldCoords[1]);
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

        camera.zoom = MathUtils.clamp(camera.zoom, 0.5f, 1.9f);

        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f, Constants.MAP_WIDTH - effectiveViewportWidth / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f, Constants.MAP_HEIGHT - effectiveViewportHeight / 2f);
    }
}
