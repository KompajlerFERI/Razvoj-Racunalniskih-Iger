package main.java.si.um.feri.kompajler.config;

public class GameConfig {
    private static final float WORLD_UNIT = 10;

    private static float WIDTH = 80;
    private static float HEIGHT = 80;

    private static float HUD_WIDTH = 80;
    private static float HUD_HEIGHT = 80;

    private static float PLAYER_WIDTH = 30;
    private static float PLAYER_HEIGHT = 30;

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
    public static void setWidth(float width) {
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
    }

    // SET IN WORLD UNITS AND NOT PIXELS
    // FOR EXAMPLE, IF YOU WANT TO SET SOMETHING
    // TO 100 PIXELS, YOU NEED TO SET IT TO 10
    // (DIVIDE BY 10)
    public static void setPlayerWidth(float playerWidth) {
        PLAYER_WIDTH = playerWidth;
    }

    // SET IN WORLD UNITS AND NOT PIXELS
    // FOR EXAMPLE, IF YOU WANT TO SET SOMETHING
    // TO 100 PIXELS, YOU NEED TO SET IT TO 10
    // (DIVIDE BY 10)
    public static void setPlayerHeight(float playerHeight) {
        PLAYER_HEIGHT = playerHeight;
    }
}
