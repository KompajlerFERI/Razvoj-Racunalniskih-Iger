package si.um.feri.kompajler.utils;

import com.badlogic.gdx.Gdx;

public class Constants {
    public static final int NUM_TILES = 5;
    public static final int ZOOM = 14;

    public static final float CAMERA_MOVEMENT_SPEED = 3f;
    public static final float CAMERA_ZOOM_SPEED = 0.01f;
    public static final float MIN_CAMERA_ZOOM = 0.5f;
    public static final float MAX_CAMERA_ZOOM = 2.5f;
    public static final int MAP_WIDTH = MapRasterTiles.TILE_SIZE * NUM_TILES;
    public static final int MAP_HEIGHT = MapRasterTiles.TILE_SIZE * NUM_TILES;
    public static final int HUD_WIDTH = Gdx.graphics.getWidth();
    public static final int HUD_HEIGHT = Gdx.graphics.getHeight();
}
