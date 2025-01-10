package si.um.feri.kompajler.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import si.um.feri.kompajler.assets.RegionNames;
import si.um.feri.kompajler.config.GameConfig;

public class Player {
    private int hitpoints;
    private TextureRegion tankBottom;
    private TextureRegion tankTop;
    private int id;
    public Rectangle rectangle;
    private float rotation;

    public Player(TextureAtlas atlas, int id) {
        this.id = id;
        this.hitpoints = 1;
        this.tankBottom = atlas.findRegion(RegionNames.TANK_BOTTOM_GREEN);
        this.tankTop = atlas.findRegion(RegionNames.TANK_TOP_GREEN);
        float width = tankBottom.getRegionWidth() * 1.5f;
        float height = tankBottom.getRegionHeight() * 1.5f;
        this.rectangle = new Rectangle(50, 50, width, height);
        this.rotation = 0;
    }

    public int getHitpoints() {
        return hitpoints;
    }

    public TextureRegion getTankBottom() {
        return tankBottom;
    }
    public TextureRegion getTankTop() {
        return tankTop;
    }
    public float getRotation() {
        return rotation;
    }

    public void playerMovement(float deltaTime) {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            rotation += 200 * deltaTime; // Rotate left
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            rotation -= 200 * deltaTime; // Rotate right
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            float radians = (float) Math.toRadians(rotation);
            float deltaX = (float) Math.cos(radians) * 100 * deltaTime * GameConfig.PLAYER_SPEED;
            float deltaY = (float) Math.sin(radians) * 100 * deltaTime * GameConfig.PLAYER_SPEED;

            rectangle.setPosition(rectangle.x + deltaX, rectangle.y + deltaY);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            float radians = (float) Math.toRadians(rotation);
            float deltaX = (float) Math.cos(radians) * 100 * deltaTime * GameConfig.PLAYER_SPEED;
            float deltaY = (float) Math.sin(radians) * 100 * deltaTime * GameConfig.PLAYER_SPEED;

            rectangle.setPosition(rectangle.x - deltaX, rectangle.y - deltaY);
        }
    }
}
