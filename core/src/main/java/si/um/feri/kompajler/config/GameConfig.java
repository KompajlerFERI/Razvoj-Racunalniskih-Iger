package si.um.feri.kompajler.config;

public class GameConfig {
    private static final float WORLD_UNIT = 10;
    public static final float WORLD_UNIT_PIXELS = 32;

    public static final float INITIAL_WINDOW_HEIGHT = 544;
    public static final float INITIAL_WINDOW_WIDTH = 1024;

    private static final float WIDTH = 80;
    private static final float HEIGHT = 80;

    public static final float HUD_WIDTH = INITIAL_WINDOW_WIDTH;
    public static final float HUD_HEIGHT = INITIAL_WINDOW_HEIGHT;

    public static final float WORLD_UNITS_WIDTH = 32f;
    public static final float WORLD_UNITS_HEIGHT = 18f;
    public static final float WORLD_UNITS_LEFT_OFFSET = 7f;

    public static final float PLAYER_WIDTH = 30;
    public static final float PLAYER_HEIGHT = 30;
    public static final float PLAYER_SPEED = 3;

    public static final float BULLET_SPEED = 7;

    private GameConfig() {
    }

    // Scaling method (from pixels to world units)
    public static float scaleToWorldUnits(float value) {
        return value * WORLD_UNIT;
    }

    // Scaling method (from world units to pixels)
    public static float scaleToPixels(float value) {
        return value / WORLD_UNIT;
    }

    // Getters

    // GET IN WORLD UNITS AND NOT PIXELS
    // FOR EXAMPLE, IF YOU WANT TO SET SOMETHING
    // TO 100 PIXELS, YOU NEED TO SET IT TO 10
    // (DIVIDE BY 10)
    public static float getWidth() {
        return WIDTH * WORLD_UNIT;
    }

    // GET IN WORLD UNITS AND NOT PIXELS
    // FOR EXAMPLE, IF YOU WANT TO SET SOMETHING
    // TO 100 PIXELS, YOU NEED TO SET IT TO 10
    // (DIVIDE BY 10)
    public static float getHeight() {
        return HEIGHT * WORLD_UNIT;
    }

    // GET IN WORLD UNITS AND NOT PIXELS
    // FOR EXAMPLE, IF YOU WANT TO SET SOMETHING
    // TO 100 PIXELS, YOU NEED TO SET IT TO 10
    // (DIVIDE BY 10)
    public static float getHudWidth() {
        return HUD_WIDTH * WORLD_UNIT;
    }

    // GET IN WORLD UNITS AND NOT PIXELS
    // FOR EXAMPLE, IF YOU WANT TO SET SOMETHING
    // TO 100 PIXELS, YOU NEED TO SET IT TO 10
    // (DIVIDE BY 10)
    public static float getHudHeight() {
        return HUD_HEIGHT * WORLD_UNIT;
    }

    // GET IN WORLD UNITS AND NOT PIXELS
    // FOR EXAMPLE, IF YOU WANT TO SET SOMETHING
    // TO 100 PIXELS, YOU NEED TO SET IT TO 10
    // (DIVIDE BY 10)
    public static float getPlayerWidth() {
        return PLAYER_WIDTH * WORLD_UNIT;
    }

    // GET IN WORLD UNITS AND NOT PIXELS
    // FOR EXAMPLE, IF YOU WANT TO SET SOMETHING
    // TO 100 PIXELS, YOU NEED TO SET IT TO 10
    // (DIVIDE BY 10)
    public static float getPlayerHeight() {
        return PLAYER_HEIGHT * WORLD_UNIT;
    }


    // SET IN WORLD UNITS AND NOT PIXELS
    // FOR EXAMPLE, IF YOU WANT TO SET SOMETHING
    // TO 100 PIXELS, YOU NEED TO SET IT TO 10
    // (DIVIDE BY 10)
    /*public static void setWidth(float width) {
        WIDTH = width;
    }

    // SET IN WORLD UNITS AND NOT PIXELS
    // FOR EXAMPLE, IF YOU WANT TO SET SOMETHING
    // TO 100 PIXELS, YOU NEED TO SET IT TO 10
    // (DIVIDE BY 10)
    public static void setHeight(float height) {
        HEIGHT = height;
    }

    // SET IN WORLD UNITS AND NOT PIXELS
    // FOR EXAMPLE, IF YOU WANT TO SET SOMETHING
    // TO 100 PIXELS, YOU NEED TO SET IT TO 10
    // (DIVIDE BY 10)
    public static void setHudWidth(float hudWidth) {
        HUD_WIDTH = hudWidth;
    }

    // SET IN WORLD UNITS AND NOT PIXELS
    // FOR EXAMPLE, IF YOU WANT TO SET SOMETHING
    // TO 100 PIXELS, YOU NEED TO SET IT TO 10
    // (DIVIDE BY 10)
    public static void setHudHeight(float hudHeight) {
        HUD_HEIGHT = hudHeight;
    }*/
}
