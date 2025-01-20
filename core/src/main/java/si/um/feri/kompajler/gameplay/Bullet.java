package si.um.feri.kompajler.gameplay;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import si.um.feri.kompajler.assets.RegionNames;
import si.um.feri.kompajler.config.GameConfig;

public class Bullet {
    private Rectangle bounds;
    private Vector2 velocity;
    private float speed;
    private TextureRegion textureRegion;
    private Color color;
    public int count;
    public int playerId;
    public boolean damageFriendly;

    public Bullet(float x, float y, float velocityX, float velocityY, TextureAtlas atlas, int playerId) {
        this.textureRegion = atlas.findRegion(RegionNames.BULLET); // Assuming "bullet" is the name of the region in the atlas
        float width = textureRegion.getRegionWidth() * 1.5f;
        float height = textureRegion.getRegionHeight() * 1.5f;
        this.speed = GameConfig.BULLET_SPEED;
        this.bounds = new Rectangle(x, y, width, height);
        this.velocity = new Vector2(velocityX, velocityY).nor().scl(speed);
        this.count = MathUtils.random(3, 7);
        this.damageFriendly = false;
        this.playerId = playerId;

        // Set color to either green, orange, or red
        int colorChoice = MathUtils.random(3);
        switch (colorChoice) {
            case 0:
                this.color = Color.GREEN;
                break;
            case 1:
                this.color = Color.ORANGE;
                break;
            case 2:
                this.color = Color.RED;
                break;
            case 3:
                this.color = Color.YELLOW;
                break;
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public Color getColor() {
        return color;
    }

    public void update(float deltaTime) {
        bounds.x += velocity.x * deltaTime;
        bounds.y += velocity.y * deltaTime;
    }

    public void bounceX() {
        velocity.x = -velocity.x;
    }

    public void bounceY() {
        velocity.y = -velocity.y;
    }

    public void setPosition(float x, float y) {
        bounds.setPosition(x, y);
    }
}
