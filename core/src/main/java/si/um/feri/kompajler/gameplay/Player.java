package main.java.si.um.feri.kompajler.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import org.w3c.dom.css.Rect;

import si.um.feri.kompajler.assets.AssetDescriptors;
import si.um.feri.kompajler.assets.RegionNames;
import si.um.feri.kompajler.config.GameConfig;

public class Player {
    private int hitpoints;
    private TextureRegion tankBottom;
    //private TextureRegion tankTop;
    private int id;
    public Rectangle rectangle;
    private Rectangle hitbox;
    private float rotation;
    public boolean moving;
    public boolean stopMovement;
    private TextureAtlas atlas;
    private AssetManager assetManager;
    private float initialX;
    private float initialY;

    public Player(TextureAtlas atlas, AssetManager assetManager, int id) {
        this.assetManager = assetManager;
        this.id = id;
        this.hitpoints = 100;
        if (id == 0) {
            this.tankBottom = atlas.findRegion(RegionNames.TANK_BOTTOM_GREEN);
            //this.tankTop = atlas.findRegion(RegionNames.TANK_TOP_GREEN);
            float width = tankBottom.getRegionWidth() * 3.2f;
            float height = tankBottom.getRegionHeight() * 3.2f;
            this.initialX = 50;
            this.initialY = 50;
            this.rectangle = new Rectangle(50, 50, width, height);
        }
        if (id == 1) {
            this.tankBottom = atlas.findRegion(RegionNames.TANK_BOTTOM_RED);
            //this.tankTop = atlas.findRegion(RegionNames.TANK_TOP_RED);
            float width = tankBottom.getRegionWidth() * 3.2f;
            float height = tankBottom.getRegionHeight() * 3.2f;
            this.initialX = 1440;
            this.initialY = 1430;
            this.rectangle = new Rectangle(1440, 1430, width, height);
        }
        this.rotation = 0;
        moving = false;
        this.atlas = atlas;
    }

    public int getHitpoints() {
        return hitpoints;
    }

    public TextureRegion getTankBottom() {
        return tankBottom;
    }
//    public TextureRegion getTankTop() {
//        return tankTop;
//    }
    public float getRotation() {
        return rotation;
    }
    public void damage() {
        hitpoints--;
    }
    public Rectangle getBounds() {
        return rectangle;
    }
    public int getId() {
        return id;
    }

    public void resetPosition(int x, int y) {
        this.rectangle.setPosition(x, y);
    }

    public void shoot() {
        assetManager.get(AssetDescriptors.SHOOT_WAV).play(0.01f);
        // Bullet dimensions
        float bulletWidth = 10;
        float bulletHeight = 10;

        // Calculate bullet's initial position (at the front of the tank)
        float bulletX = rectangle.x + rectangle.width / 2 - bulletWidth / 2;
        float bulletY = rectangle.y + rectangle.height / 2 - bulletHeight / 2;

        // Calculate bullet velocity based on player's rotation
        float radians = (float) Math.toRadians(rotation);
        float bulletSpeed = GameConfig.BULLET_SPEED;
        float velocityX = (float) Math.cos(radians) * bulletSpeed;
        float velocityY = (float) Math.sin(radians) * bulletSpeed;

        // Create and add bullet to the bullets array
        Bullet bullet = new Bullet(bulletX, bulletY, velocityX, velocityY, atlas, id);
        GameManager.getInstance().bullets.add(bullet);
    }
    public void playerMovement(float deltaTime) {
        if (id == 0) {
            moving = false;
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                rotation += 200 * deltaTime; // Rotate left
                moving = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                rotation -= 200 * deltaTime; // Rotate right
                moving = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.W) && !stopMovement) {
                float radians = (float) Math.toRadians(rotation);
                float deltaX = (float) Math.cos(radians) * 100 * deltaTime * GameConfig.PLAYER_SPEED;
                float deltaY = (float) Math.sin(radians) * 100 * deltaTime * GameConfig.PLAYER_SPEED;

                rectangle.setPosition(rectangle.x + deltaX, rectangle.y + deltaY);
                moving = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S) && !stopMovement) {
                float radians = (float) Math.toRadians(rotation);
                float deltaX = (float) Math.cos(radians) * 100 * deltaTime * GameConfig.PLAYER_SPEED;
                float deltaY = (float) Math.sin(radians) * 100 * deltaTime * GameConfig.PLAYER_SPEED;

                rectangle.setPosition(rectangle.x - deltaX, rectangle.y - deltaY);
                moving = true;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
                System.out.println("PEW");
                shoot();
            }

            stopMovement = false;
        }
        else if (id == 1) {
            moving = false;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                rotation += 200 * deltaTime; // Rotate left
                moving = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                rotation -= 200 * deltaTime; // Rotate right
                moving = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP) && !stopMovement) {
                float radians = (float) Math.toRadians(rotation);
                float deltaX = (float) Math.cos(radians) * 100 * deltaTime * GameConfig.PLAYER_SPEED;
                float deltaY = (float) Math.sin(radians) * 100 * deltaTime * GameConfig.PLAYER_SPEED;

                rectangle.setPosition(rectangle.x + deltaX, rectangle.y + deltaY);
                moving = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && !stopMovement) {
                float radians = (float) Math.toRadians(rotation);
                float deltaX = (float) Math.cos(radians) * 100 * deltaTime * GameConfig.PLAYER_SPEED;
                float deltaY = (float) Math.sin(radians) * 100 * deltaTime * GameConfig.PLAYER_SPEED;

                rectangle.setPosition(rectangle.x - deltaX, rectangle.y - deltaY);
                moving = true;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
                System.out.println("PEW");
                shoot();
            }

            stopMovement = false;
        }
    }
}
